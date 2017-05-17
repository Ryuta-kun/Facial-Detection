package Nayna;

import static org.bytedeco.javacpp.opencv_contrib.createLBPHFaceRecognizer;
import static org.bytedeco.javacpp.opencv_core.CV_32SC1;
import static org.bytedeco.javacpp.opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE;
import static org.bytedeco.javacpp.opencv_highgui.imread;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger; 
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import org.bytedeco.javacpp.opencv_contrib.FaceRecognizer;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;


public class RealTime2 extends javax.swing.JFrame {

	private JButton startButton;
	private JButton stopButton;
	private JButton captureImage;
	private JButton trainer;
	private JPanel videoField;
	private JTextField name;
	private int defaultInt = 0;
	private Detection2 pre = new Detection2();
	private int im_width;
	private int im_height;
	private Mat face_resized = new Mat();

	private DaemonThread randomRunningThread = null;
	int count = 0;
	VideoCapture theVideo = null;
	Mat theFrame = new Mat();
	MatOfByte mem = new MatOfByte();
	CascadeClassifier faceDetector = new CascadeClassifier(("C:\\Users\\wodbr\\haarcascade_frontalface_alt.xml"));
	MatOfRect faceDetection2s = new MatOfRect();
	Graphics g;
	Image im;

	class DaemonThread implements Runnable {
		public volatile boolean validCamera = false;
		Mat videoMatGray = new Mat();
		@Override
		public void run() {
			synchronized (this) {
				while (validCamera) {
					if (theVideo.grab()) {
						try {
							theVideo.retrieve(theFrame);
							g = videoField.getGraphics();
							Imgproc.cvtColor(theFrame, videoMatGray, Imgproc.COLOR_RGB2GRAY);
							faceDetector.detectMultiScale(videoMatGray, faceDetection2s, 1.1, 5, Objdetect.CASCADE_SCALE_IMAGE, new Size(150, 150), new Size(400,400));
							for (Rect face : faceDetection2s.toArray()) {
								Mat cface = new Mat(videoMatGray, face);
								Imgproc.resize(cface, face_resized, new Size(250, 250), 1.0, 1.0, Imgproc.INTER_CUBIC);
								String predict = pre.blah2(face_resized);
								if (predict.equals("-1")){
									predict = "unknown";
								}
								Core.rectangle(theFrame, new Point(face.x, face.y), new Point(face.x + face.width, face.y + face.height),
										new Scalar(0, 255,0));
								String box_text = "Prediction = " + predict;
								int pos_x = Math.max(face.x - 10, 0);
								int pos_y = Math.max(face.y - 10, 0);
								pre.blah3(theFrame,  box_text,  pos_x,  pos_y);
							}

							Highgui.imencode(".bmp", theFrame, mem);
							im = ImageIO.read(new ByteArrayInputStream(mem.toArray()));
							BufferedImage buff = (BufferedImage) im;

							if (g.drawImage(buff, 0, 0, getWidth(), getHeight()-150 , 0, 0, buff.getWidth(), buff.getHeight(), null)) {
								if (validCamera == false) {
									this.wait();
								}
							}
						} catch (Exception e) {
							System.out.println("Something is wrong.");
						}
					}
				}
			}
		}
	}


	public RealTime2() {
		theLook();
	}


	public void theLook() {
		videoField = new JPanel();
		startButton = new JButton();
		stopButton = new JButton();
		trainer = new JButton();

		captureImage = new JButton();
		name = new JTextField(30);



		startButton.setText("Start");
		stopButton.setText("Stop");
		captureImage.setText("Capture");
		trainer.setText("Trainer");

		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				startPerformed(evt);
			}
		});
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				stopPerformed(evt);
			}
		});
		captureImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				capturePerformed(evt);
			}
		});

		trainer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				predictPerformed(evt);
			}
		});

		Container nameHolder = new Container();

		BorderLayout captureName = new BorderLayout();
		nameHolder.setLayout(captureName);
		nameHolder.add(captureImage, BorderLayout.CENTER);
		nameHolder.add(name, BorderLayout.EAST);
		nameHolder.add(trainer, BorderLayout.WEST);

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		BorderLayout layout = new BorderLayout();
		getContentPane().setLayout(layout);

		add(videoField, BorderLayout.CENTER);
		add(startButton, BorderLayout.WEST);
		add(stopButton, BorderLayout.EAST);
		add(nameHolder, BorderLayout.SOUTH);

		setPreferredSize(new Dimension(1200,600));
		setResizable(false);
		pack();
	}


	private void capturePerformed(ActionEvent evt){
		String imageName = name.getText();
		Detection2 Counter = new Detection2();
		defaultInt = Counter.setDefault();
		if(imageName.equals("")){
			Highgui.imwrite("C:\\Users\\wodbr\\Desktop\\eclipse\\PlayingGames\\TestImages\\"+defaultInt+"-"+"default.jpg", face_resized);
			defaultInt++;
		}else{
			Highgui.imwrite("C:\\Users\\wodbr\\Desktop\\eclipse\\PlayingGames\\TestImages\\"+defaultInt+"-"+imageName + ".jpg", face_resized);
			defaultInt++;
		}
	}

	private void predictPerformed(ActionEvent evt){
		String x = "";
		JFrame person = new JFrame();
		Highgui.imwrite("C:\\Users\\wodbr\\Desktop\\eclipse\\Images\\Predictor.jpg", face_resized);
		pre.setImage("Predictor");
		x = pre.blah();
		JOptionPane.showMessageDialog(person, "The Individual here is " +x+"!!");
	}
	

	private void startPerformed(ActionEvent evt) {

		//Video Camera to take pictures
		theVideo = new VideoCapture(0);
		pre.trainee();
		randomRunningThread = new DaemonThread();
		Thread t = new Thread(randomRunningThread);
		t.setDaemon(true);
		randomRunningThread.validCamera = true;
		t.start();

		//Rotate the Buttons.
		startButton.setEnabled(false);
		stopButton.setEnabled(true);


	}

	private void stopPerformed(ActionEvent evt) {
		randomRunningThread.validCamera = false;

		//Rotate the Buttons.
		startButton.setEnabled(true); 
		stopButton.setEnabled(false);


		//Pause the Camera to take a picture.
		theVideo.release();
	}


	public static void main(String args[]) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new RealTime2().setVisible(true);
			}
		});
	}
}
