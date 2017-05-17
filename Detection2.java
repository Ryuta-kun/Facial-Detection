package Nayna;

import static org.bytedeco.javacpp.opencv_contrib.createLBPHFaceRecognizer;
import static org.bytedeco.javacpp.opencv_contrib.createFisherFaceRecognizer;
import static org.bytedeco.javacpp.opencv_core.CV_32SC1;
import static org.bytedeco.javacpp.opencv_core.FONT_HERSHEY_PLAIN;
import static org.bytedeco.javacpp.opencv_core.putText;
import static org.bytedeco.javacpp.opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE;
import static org.bytedeco.javacpp.opencv_highgui.imread;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.IntBuffer;

import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.opencv_contrib.FaceRecognizer;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Scalar;

public class Detection2 {
	String thePerson = "";
	String Predict = "";
	String trainingDir = "C:\\Users\\wodbr\\Desktop\\eclipse\\PlayingGames\\TestImages";
	//FaceRecognizer faceRecognizer = createLBPHFaceRecognizer();
	FaceRecognizer faceRecognizer = createFisherFaceRecognizer();
	   public String blah() {
	        Mat testImage = imread("C:\\Users\\wodbr\\Desktop\\eclipse\\Images\\" +Predict +".jpg", CV_LOAD_IMAGE_GRAYSCALE);
	        File root = new File(trainingDir);

	        FilenameFilter imgFilter = new FilenameFilter() {
	            public boolean accept(File dir, String name) {
	                name = name.toLowerCase();
	                return name.endsWith(".jpg") || name.endsWith(".pgm") || name.endsWith(".png");
	            }
	        };

	        File[] imageFiles = root.listFiles(imgFilter);

	        MatVector images = new MatVector(imageFiles.length);

	        Mat labels = new Mat(imageFiles.length, 1, CV_32SC1);
	        IntBuffer labelsBuf = labels.getIntBuffer();

	        int counter = 0;

	        for (File image : imageFiles) {
	            Mat img = imread(image.getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE);

	            int label = Integer.parseInt(image.getName().split("\\-")[0]);

	            images.put(counter, img);

	            labelsBuf.put(counter, label);

	            counter++;
	        }

	        //FaceRecognizer faceRecognizer = createFisherFaceRecognizer();
	        //FaceRecognizer faceRecognizer1 = createEigenFaceRecognizer();
	        FaceRecognizer faceRecognizer = createLBPHFaceRecognizer();

	        faceRecognizer.train(images, labels);

	        int predictedLabel = faceRecognizer.predict(testImage);
	        for (File image : imageFiles) {
	            Mat img = imread(image.getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE);

	            int label2 = Integer.parseInt(image.getName().split("\\-")[0]);
	            
	            if (label2 == predictedLabel){
	            	System.out.println(label2);
	            	thePerson = image.getName();
	            	thePerson = thePerson.substring(2, thePerson.length()-4);
	            }
	        }
	        System.out.println(predictedLabel);
	        return thePerson;
	    }
	   
	   public void setImage(String predictor){
		   this.Predict = predictor;
	   }
	   
	   public String blah2(org.opencv.core.Mat pre){
		   Mat cface = new Mat((Pointer)null) {{ address = pre.getNativeObjAddr();}};
		   int prediction = faceRecognizer.predict(cface);
		   
		   String trainingDir = "C:\\Users\\wodbr\\Desktop\\eclipse\\PlayingGames\\TestImages";
	       File root = new File(trainingDir);
		   
		   FilenameFilter imgFilter = new FilenameFilter() {
	            public boolean accept(File dir, String name) {
	                name = name.toLowerCase();
	                return name.endsWith(".jpg") || name.endsWith(".pgm") || name.endsWith(".png");
	            }
	        };

	       File[] imageFiles = root.listFiles(imgFilter);
	        
		   for (File image : imageFiles) {
	            Mat img = imread(image.getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE);

	            int label2 = Integer.parseInt(image.getName().split("\\-")[0]);
	            
	            if (label2 == prediction){
	            	//System.out.println(label2);
	            	thePerson = image.getName();
					if(label2 >9 &&label2 < 100){

						thePerson = thePerson.substring(3, thePerson.length()-4);
						return thePerson;
					}
					if (label2 <1000 && label2 > 99){
						thePerson = thePerson.substring(4, thePerson.length()-4);
						return thePerson;
					}

					if (label2 <10 && label2 > -1){
						thePerson = thePerson.substring(2, thePerson.length()-4);
						return thePerson;
					}
	            }
	        }
		   return thePerson;
	   }
	   
	   public void blah3(org.opencv.core.Mat pre, String box_text, int pos_x, int pos_y){
		   Mat theFrame = new Mat((Pointer)null) {{ address = pre.getNativeObjAddr();}};
		   putText(theFrame, box_text, new Point(pos_x, pos_y), FONT_HERSHEY_PLAIN, 1.0, new Scalar(0,255,0,2.0));
	   }
	   
	   public void trainee(){
	        File root = new File(trainingDir);

	        FilenameFilter imgFilter = new FilenameFilter() {
	            public boolean accept(File dir, String name) {
	                name = name.toLowerCase();
	                return name.endsWith(".jpg") || name.endsWith(".pgm") || name.endsWith(".png");
	            }
	        };

	        File[] imageFiles = root.listFiles(imgFilter);

	        MatVector images = new MatVector(imageFiles.length);

	        Mat labels = new Mat(imageFiles.length, 1, CV_32SC1);
	        IntBuffer labelsBuf = labels.getIntBuffer();

	        int counter = 0;

	        for (File image : imageFiles) {
	            Mat img = imread(image.getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE);

	            int label = Integer.parseInt(image.getName().split("\\-")[0]);

	            images.put(counter, img);

	            labelsBuf.put(counter, label);

	            counter++;
	        }

	        faceRecognizer.train(images, labels);
	   }
	   
		public int setDefault(){
			String trainingDir = "C:\\Users\\wodbr\\Desktop\\eclipse\\PlayingGames\\TestImages";
			File root = new File(trainingDir);
			FilenameFilter imgFilter = new FilenameFilter() {
				public boolean accept(File dir, String name) {
					name = name.toLowerCase();
					return name.endsWith(".jpg") || name.endsWith(".pgm") || name.endsWith(".png");
				}
			};
			File[] imageFiles = root.listFiles(imgFilter);
			int counter = 0;
			for (File image : imageFiles) {
				counter++;
			}
			return counter;
		}
}
