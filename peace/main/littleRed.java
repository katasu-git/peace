import java.net.*;
import java.io.*;
import javax.swing.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.awt.Color;

//�g��
import javax.swing.border.LineBorder;
import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;

import java.io.File;//���y�Đ����ɕK�v
import javax.sound.sampled.AudioFormat;//���y�Đ����ɕK�v
import javax.sound.sampled.AudioSystem;//���y�Đ����ɕK�v
import javax.sound.sampled.Clip;//���y�Đ����ɕK�v
import javax.sound.sampled.DataLine;//���y�Đ����ɕK�v

//�^�C�}�[
import java.text.ParseException;
import java.util.Timer;
import java.util.TimerTask;

public class littleRed extends JFrame implements MouseListener,MouseMotionListener,ActionListener {
	private JButton buttonArray[][];//�{�^���p�̔z��
	private Container c;
	private ImageIcon whiteIcon, redIcon, boardIcon,pactiveIcon,ractiveIcon,
			passIcon, circleIcon, guideIcon, redHoodIcon, wolfIcon, wmicon, rmicon;
	private int myColor;
	private int myTurn = 3; //myTurn==3�̂Ƃ������^�[���Ƃ���BmyTurn==0�͍��A1�͔��B
	private int myIconCount = 2, yourIconCount = 2, countSub =0;
	private int passCount = 0;
	private int guideCount = 0;
	private ImageIcon myIcon, yourIcon;
	private int flipNum = 0;
	private JButton passButton, resetButton, pactiveButton, ractiveButton;
	private String chara = "";
	static int TurnCount = 0;
	static String winnerStr = "wolf"; // or "red" or "draw"
	static int difCounter = 3; // 0 = ����5�𒴂��Ă��Ȃ� 1 = ������x�ł�5�ȏ�ɂȂ��� 2 = ����10�ȏ�ɂȂ��� //3=>OP����
	static int op = 0;
	SoundPlayer theSoundPlayer1;//�ǂ�����ł��A�N�Z�X�ł���悤�ɁC�N���X�̃����o�Ƃ��Đ錾
	PrintWriter out;//�o�͗p�̃��C�^�[

	//�|�C���^�[�̃A�C�R��
	ImageIcon pointerIcon = new ImageIcon("icons/arrow.png");
	JLabel pointerLabel = new JLabel(pointerIcon);

	//�|�C���^�[�̃J�E���g
	JLabel pointcon = new JLabel();

	//�^�[���������A�C�R��
	ImageIcon myturnIcon = new ImageIcon("icons/yourturn.png");
	ImageIcon rivalTurnIcon = new ImageIcon("icons/rivalturn.png");
	JLabel turnLabel = new JLabel(myturnIcon);

	//�Ó]�p�̉摜
	static ImageIcon back90Icon = new ImageIcon("icons/background90.png");
	static JLabel back90 = new JLabel(back90Icon);

	//���O
	JLabel strow1 = new JLabel();
	JLabel strow2 = new JLabel();
	JLabel strow3 = new JLabel();
	JLabel strow4 = new JLabel();

	JLabel comrow1 = new JLabel();
	JLabel comrow2 = new JLabel();

	JLabel leftTurn = new JLabel();

	public littleRed() {

		/*
		//���O�̓��̓_�C�A���O���J��
		String myName = JOptionPane.showInputDialog(null,"���O����͂��Ă�������","���O�̓���",JOptionPane.QUESTION_MESSAGE);
		if(myName.equals("")){
			myName = "No name";//���O���Ȃ��Ƃ��́C"No name"�Ƃ���
		}
		*/

		String myName = "No name";

		//IP�A�h���X�̓���
		String myIp = JOptionPane.showInputDialog(null,"IP�A�h���X����́B�I�t���C���̏ꍇ�͉������͂��Ȃ���OK","IP�A�h���X�̓���",JOptionPane.QUESTION_MESSAGE);
		if(myIp.equals("")){
			myIp = "localhost";//�Ȃ��Ƃ��́Clocalhost�Ƃ���
		}

		setUp();

		//�T�[�o�ɐڑ�����
		Socket socket = null;
		try {
			//"localhost"�́C���������ւ̐ڑ��Dlocalhost��ڑ����IP Address�i"133.42.155.201"�`���j�ɐݒ肷��Ƒ���PC�̃T�[�o�ƒʐM�ł���
			//10000�̓|�[�g�ԍ��DIP Address�Őڑ�����PC�����߂āC�|�[�g�ԍ��ł���PC�㓮�삷��v���O��������肷��
			socket = new Socket(myIp, 10000);
		} catch (UnknownHostException e) {
			System.err.println("�z�X�g�� IP �A�h���X������ł��܂���: " + e);
		} catch (IOException e) {
			System.err.println("�G���[���������܂���: " + e);
		}

		MesgRecvThread mrt = new MesgRecvThread(socket, myName);//��M�p�̃X���b�h���쐬����
		mrt.start();//�X���b�h�𓮂����iRun�������j
	}

	//���b�Z�[�W��M�̂��߂̃X���b�h
	public class MesgRecvThread extends Thread {

		Socket socket;
		String myName;

		public MesgRecvThread(Socket s, String n){
			socket = s;
			myName = n;
		}

		//�ʐM�󋵂��Ď����C��M�f�[�^�ɂ���ē��삷��
		public void run() {
			try{
				InputStreamReader sisr = new InputStreamReader(socket.getInputStream());
				BufferedReader br = new BufferedReader(sisr);
				out = new PrintWriter(socket.getOutputStream(), true);
				out.println(myName);//�ڑ��̍ŏ��ɖ��O�𑗂�

				String myNumberStr = br.readLine();
				int myNumberInt = Integer.parseInt(myNumberStr);

				c.add(turnLabel);
				turnLabel.setBounds(180,540,126,36);
				turnLabel.setIcon(rivalTurnIcon);

				//�I�I�J�~
				wolfIcon = new ImageIcon("icons/wolf.png");
				JLabel wolfLab = new JLabel(wolfIcon);
				c.add(wolfLab);

				//�������A�C�R��
				wmicon = new ImageIcon("icons/wolf-mini.png");
				JLabel wolfminiLab = new JLabel(wmicon);
				c.add(wolfminiLab);

				//����������
				redHoodIcon = new ImageIcon("icons/redhood.png");
				JLabel redHoodLab = new JLabel(redHoodIcon);
				c.add(redHoodLab);

				//�������A�C�R��
				rmicon = new ImageIcon("icons/redhood-mini.png");
				JLabel redminilab = new JLabel(rmicon);
				c.add(redminilab);

				if(myNumberInt % 2 == 0){
					myColor = 0;
					myIcon = whiteIcon;
					yourIcon = redIcon;
					setTurn();

					//���Ȃ��̓I�I�J�~
					wolfLab.setBounds(180,410,125,125);
					wolfminiLab.setBounds(365,10,50,50);

					//�����Ă͂���������
					redHoodLab.setBounds(30,410,125,125);
					redminilab.setBounds(365,340,50,50);
					comrow1.setText("�L�~�̓I�I�J�~");

				} else {
					myColor = 1;
					myIcon = redIcon;
					yourIcon = whiteIcon;
					setTurn();

					//���Ȃ��͂���������
					redHoodLab.setBounds(180,410,125,125);
					redminilab.setBounds(365,10,50,50);

					//�����Ă̓I�I�J�~
					wolfLab.setBounds(30,410,125,125);
					wolfminiLab.setBounds(365,340,50,50);
					comrow1.setText("�A�i�^�͂���������");

				}
				//�w�i�摜�͍Ō�ɒ�`����
				ImageIcon mainImIcon = new ImageIcon("icons/main-frame.jpg");
				JLabel mainImLabel = new JLabel(mainImIcon);
				c.add(mainImLabel);
				mainImLabel.setBounds(0,0,800,600);

				while(true) {
					String inputLine = br.readLine();//�f�[�^����s�������ǂݍ���ł݂�
					if (inputLine != null) {//�ǂݍ��񂾂Ƃ��Ƀf�[�^���ǂݍ��܂ꂽ���ǂ������`�F�b�N����
						//System.out.println(inputLine);//�f�o�b�O�i����m�F�p�j�ɃR���\�[���ɏo�͂���
						String[] inputTokens = inputLine.split(" ");	//���̓f�[�^����͂��邽�߂ɁA�X�y�[�X�Ő؂蕪����
						String cmd = inputTokens[0];//�R�}���h�̎��o���D�P�ڂ̗v�f�����o��
						if(cmd.equals("MOVE")){
							//�K�v�̂Ȃ�����
						} else if(cmd.equals("PLACE")) {

							String theBName = inputTokens[1];
							int theBnum = Integer.parseInt(theBName);
							int theColor = Integer.parseInt(inputTokens[2]);
							int i = theBnum % 8;
							int j = theBnum / 8;

							if(myTurn == 3){
								if(theColor == myColor){
									//���M��
									buttonArray[i][j].setIcon(myIcon);
									myIconCount++; //�����̃J�E���g�𑝂₷
								} else {
									//���M��
									buttonArray[i][j].setIcon(yourIcon);
									yourIconCount++; //����̃J�E���g�𑝂₷
								}
								myTurn = 1;
							} else if(myTurn == 0){
								if(theColor == myColor){
									//���M��
									buttonArray[i][j].setIcon(myIcon);
									myIconCount++; //�����̃J�E���g�𑝂₷
								} else {
									//���M��
									buttonArray[i][j].setIcon(yourIcon);
									yourIconCount++; //����̃J�E���g�𑝂₷
								}
								myTurn = 1;
							} else {
								if(theColor == myColor){
									//���M��
									buttonArray[i][j].setIcon(myIcon);
									myIconCount++;
								} else {
									//���M��N���C�A���g�ł̏���
									buttonArray[i][j].setIcon(yourIcon);
									yourIconCount++;
								}
								myTurn = 0;
							}
							movePointer(myIconCount, yourIconCount); //����Ƃ̍����Z�o
							endTurn();

						} else if(cmd.equals("FLIP")) {

							String theBname = inputTokens[1];
							int theBnum = Integer.parseInt(theBname);
							int theColor = Integer.parseInt(inputTokens[2]);
							int i = theBnum % 8;
							int j = theBnum / 8;

							if(myTurn == 3){
								if(theColor == myColor){
									//���M��
									myIconCount++;
									yourIconCount--;
									buttonArray[i][j].setIcon(myIcon);
								} else {
									//���M��
									yourIconCount++;
									myIconCount--;
									buttonArray[i][j].setIcon(yourIcon);
								}
							} else if(myTurn == 0){
								if(theColor == myColor){
									//���M��
									myIconCount++;
									yourIconCount--;
									buttonArray[i][j].setIcon(myIcon);
								} else {
									//���M��
									yourIconCount++;
									myIconCount--;
									buttonArray[i][j].setIcon(yourIcon);
								}
							} else {
								if(theColor == myColor){
									//���M��
									myIconCount++;
									yourIconCount--;
									buttonArray[i][j].setIcon(myIcon);
								} else {
									//���M��
									yourIconCount++;
									myIconCount--;
									buttonArray[i][j].setIcon(yourIcon);
								}
							}
						}else if(cmd.equals("PASS")){
							int theTurn = Integer.parseInt(inputTokens[1]);//myTurn
							int theColor = Integer.parseInt(inputTokens[2]);//myColor

							//���^�[���Ńp�X
							if(myTurn == 3){
								myTurn = 1;
							} else if(myTurn == 0){
								myTurn = 1;
							} else {
								myTurn = 0;
							}
							endTurn();

						} else if(cmd.equals("DIF")) {
							//DIF�̏���
							int countSub = Integer.parseInt(inputTokens[1]);
							if(TurnCount < 12 && countSub >= 9){
								difCounter = 2;
							} else if(difCounter != 2 && countSub >= 4) {
								difCounter = 1;
							}

						} else if(cmd.equals("JUDGE")){
							testDialog();
							System.exit(0); //�����I���̏���

						} else if(cmd.equals("GUIDE")){
							int theGuide = Integer.parseInt(inputTokens[1]);//guideCount
							guideCount = theGuide; //�K�C�h�̐��𗼕��ɓK���A���L����

							//�u���Ȃ���Ώ��s����
							if(guideCount == 0) {
								whichWin();
								if(getWhichTurn()){
									String msg = "JUDGE";
									//�T�[�o�ɏ��𑗂�
									out.println(msg);
									out.flush();
									repaint();
								}
							}

						}
					}else{
						break;
					}

				}
				socket.close();
			} catch (IOException e) {
				System.err.println("�G���[���������܂���: " + e);
			}
		}
	}

	public static void main(String[] args) {
		littleRed net = new littleRed();
		net.setVisible(true);
	}

	public void mouseClicked(MouseEvent e) {
		JButton theButton = (JButton)e.getComponent();
		Icon theIcon = theButton.getIcon();

		if(theIcon.equals(boardIcon) || theIcon.equals(guideIcon)){
			String theArrayIndex = theButton.getActionCommand();//�{�^���̔z��̔ԍ������o��
			int temp = Integer.parseInt(theArrayIndex);
			int tempx = temp / 8;
			int tempy = temp % 8;
			if(judgeButton(tempy, tempx)){
				//������
				if(getWhichTurn()){
					String msg = "PLACE" + " " + theArrayIndex + " " + myColor + " " + theIcon;
					//�T�[�o�ɏ��𑗂�
					out.println(msg);
					out.flush();
					repaint();
				} else {
					playSound("sounds/cantPlace.wav");
				}

			} else {
				//�u���Ȃ�
				playSound("sounds/cantPlace.wav");
			}
			repaint();

		} else if(theIcon.equals(pactiveIcon)){ //active��Ԃ̃A�C�R���Ɏw��

				if(getWhichTurn()){
					if(passCount == 0){
						passCount++; //�p�X�J�E���g�𑝂₷
						String msg = "PASS" + " " + myTurn + " " + myColor;
						out.println(msg);
						out.flush();
						repaint();
					} else {
						playSound("sounds/cantPlace.wav");
					}
				} else {
					playSound("sounds/cantPlace.wav");
				}

		}

	}

	//�}�E�X���������Ƃ��̏���
	public void mouseEntered(MouseEvent e) {

		JButton theButton = (JButton)e.getComponent();
		Icon theIcon = theButton.getIcon();

		if(theIcon.equals(boardIcon) || theIcon.equals(guideIcon) || theIcon.equals(redIcon) ||theIcon.equals(whiteIcon)){
		//�J�^�J�^�炷
		playSound("sounds/kot.wav");

		} else	if (theIcon.equals(passIcon)){
			//�d�˂Ă����ĕ\���E��\����؂�ւ���
			passButton.setVisible(false);
			pactiveButton.setVisible(true);

		}
	}

	//�}�E�X���o���Ƃ��̏���
	public void mouseExited(MouseEvent e) {
		JButton theButton = (JButton)e.getComponent();
		Icon theIcon = theButton.getIcon();
		if(theIcon.equals(boardIcon) || theIcon.equals(guideIcon) || theIcon.equals(redIcon) ||theIcon.equals(whiteIcon)){

		} else	if (theIcon.equals(pactiveIcon)){
			//�F���]
			passButton.setVisible(true);
			pactiveButton.setVisible(false);

		} else if (theIcon.equals(ractiveIcon)){
			//�F���]
			resetButton.setVisible(true);
			ractiveButton.setVisible(false);

		}
	}

	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseDragged(MouseEvent e) {}
	public void mouseMoved(MouseEvent e) {}

	public boolean judgeButton(int y, int x){
		boolean flag = false;
		Icon IconComp;
			for(int i=-1; i<=1; i++){
				for(int j=-1; j<=1; j++){
					if(flipButtons(y, x, j, i) >= 1){
						//��ȏ㗠�Ԃ���ꍇ
						IconComp = buttonArray[y+j][x+i].getIcon();
						flag = true;

						for(int dy=j, dx=i, k=0; k<flipNum; k++, dy+=j, dx+=i){
							//�{�^���̈ʒu�������
							int msgy = y + dy;
							int msgx = x + dx;
							int theArrayIndex = msgx*8 + msgy;

							if(getWhichTurn()){
								String msg = "FLIP"+" "+theArrayIndex+" "+myColor;
								out.println(msg);
								out.flush();
								repaint();
							} else {
								playSound("sounds/cantPlace.wav");
							}

						}
					} else {
						//�ЂƂ����Ԃ��Ȃ�
					}
				}
			}
			return flag;
	}

	public int flipButtons(int y, int x, int j, int i){
		Icon IconRev;
		flipNum = 0; //������
		for(int dy=j, dx=i; ; dy+=j, dx+=i) {
			try{
				IconRev = buttonArray[(y+dy)][(x+dx)].getIcon();
				if(dy == 0){
					if(dx == 0){
						flipNum = 0;
						break;
					}
				}

				if(IconRev.equals(boardIcon)){
				flipNum = 0;
				break;
				} else if(IconRev.equals(myIcon)) {
					break;
				} else if(IconRev.equals(yourIcon)){
					flipNum++;
				} else if(IconRev.equals(guideIcon)){
					flipNum = 0;
					break;
				}
			}catch(ArrayIndexOutOfBoundsException e){
				flipNum = 0;
				break;
			}
		}
		return flipNum;
	}

	//UI�̐���
	public void setUp(){
		//�E�B���h�E���쐬
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("little Red");//�E�B���h�E�̃^�C�g��
		setSize(812,635);//�E�B���h�E�̃T�C�Y
		c = getContentPane();

		//fix
		testDialog();
		difCounter = 0;
		if(op == 1){
			System.exit(0);
		}

		//�A�C�R���̐ݒ�
		redIcon = new ImageIcon("icons/r-icon.png");
		whiteIcon = new ImageIcon("icons/w-icon.png");
		boardIcon = new ImageIcon("icons/b-icon.png");
		passIcon = new ImageIcon("icons/pass.png");
		pactiveIcon = new ImageIcon("icons/pactive.png");
		circleIcon = new ImageIcon("icons/circle.png");
		guideIcon = new ImageIcon("icons/g-icon.png");

		//�Ó]�p�̉摜
		c.add(back90);
		back90.setBounds(0,0,800,600);
		back90.setVisible(false);

		c.setLayout(null);//�������C�A�E�g�̐ݒ���s��Ȃ�
		//�{�^���̐���
		buttonArray = new JButton[8][8];

		for(int j=0;j<8;j++){
			for(int i=0;i<8;i++){
			buttonArray[i][j] = new JButton(boardIcon);//�{�^���ɃA�C�R����ݒ肷��
			c.add(buttonArray[i][j]);//�y�C���ɓ\��t����

			buttonArray[i][j].setBounds(i*45+428,j*45+15,45,45);//�{�^���̑傫���ƈʒu��ݒ肷��D(x���W�Cy���W,x�̕�,y�̕��j
			buttonArray[i][j].addMouseListener(this);//�{�^�����}�E�X�ł�������Ƃ��ɔ�������悤�ɂ���
			buttonArray[i][j].addMouseMotionListener(this);//�{�^�����}�E�X�œ��������Ƃ����Ƃ��ɔ�������悤�ɂ���
			buttonArray[i][j].setActionCommand(Integer.toString(j*8+i));//�{�^���ɔz��̏���t������i�l�b�g���[�N����ăI�u�W�F�N�g�����ʂ��邽�߁j
			buttonArray[i][j].setContentAreaFilled(false); //�{�^���w�i�̓�����
		  }
		}

		buttonArray[3][3].setIcon(redIcon);
		buttonArray[4][3].setIcon(whiteIcon);
		buttonArray[3][4].setIcon(whiteIcon);
		buttonArray[4][4].setIcon(redIcon);

		c.add(pointcon);
		pointcon.setBounds(398,180,30,30);
		pointcon.setText(" " + Integer.toString(countSub));
		pointcon.setForeground(Color.decode("#c0bfbf"));
		//pointcon.setOpaque(true); //�w�i������
		pointcon.setFont(new Font("UD �f�W�^�� ���ȏ��� N-B", Font.BOLD, 15));

		c.add(pointerLabel);
		pointerLabel.setBounds(360-40+13,200-70+35,60,60);

		//���O�̒��g �ق��ł��g���̂ŃO���[�o���Œ�`�B
		//JLabel strow1 = new JLabel();
		c.add(strow1);
		strow1.setBounds(20,-10,300,300);
		strow1.setText("�����͋��ʂ�");
		strow1.setFont(new Font("UD �f�W�^�� ���ȏ��� N-B", Font.BOLD, 16));
		strow1.setForeground(new Color(192,191,191,255));

		c.add(strow2);
		strow2.setBounds(20,50,300,300);
		strow2.setText("�X�g�[���[�{�[�h�ł�");
		strow2.setFont(new Font("UD �f�W�^�� ���ȏ��� N-B", Font.BOLD, 16));
		strow2.setForeground(new Color(192,191,191,255));

		c.add(strow3);
		strow3.setBounds(20,110,300,300);
		strow3.setText("����̐i�s�ƂƂ���");
		strow3.setFont(new Font("UD �f�W�^�� ���ȏ��� N-B", Font.BOLD, 16));
		strow3.setForeground(new Color(192,191,191,255));

		//������MAX15����
		c.add(strow4);
		strow4.setBounds(20,170,300,300);
		strow4.setText("�e�L�X�g���ω����܂�");
		strow4.setFont(new Font("UD �f�W�^�� ���ȏ��� N-B", Font.BOLD, 16));
		strow4.setForeground(new Color(192,191,191,255));

		c.add(comrow1);
		comrow1.setBounds(365,412,200,100);
		comrow1.setFont(new Font("UD �f�W�^�� ���ȏ��� N-B", Font.BOLD, 16));
		comrow1.setForeground(new Color(192,191,191,255));

		c.add(comrow2);
		comrow2.setBounds(384,452,200,100);
		comrow2.setText("���˂ȃ��^����");
		comrow2.setFont(new Font("UD �f�W�^�� ���ȏ��� N-B", Font.BOLD, 16));
		comrow2.setForeground(new Color(192,191,191,255));

		//�p�X�{�^��
		passButton = new JButton(passIcon);
		c.add(passButton);
		passButton.setBounds(550,390,100,100);
		passButton.setOpaque(true);
		passButton.addMouseListener(this);
		passButton.setContentAreaFilled(false);
		passButton.setVisible(true);
		passButton.setBorder(new LineBorder(new Color(102,102,102,255), 1, true)); //�g�̐F�ݒ�

		//�A�N�e�B�u���
		pactiveButton = new JButton(pactiveIcon);
		c.add(pactiveButton);
		pactiveButton.setBounds(550,390,100,100);
		pactiveButton.setOpaque(true);
		pactiveButton.addMouseListener(this);
		pactiveButton.setContentAreaFilled(false);
		pactiveButton.setVisible(false);
		pactiveButton.setBorder(new LineBorder(new Color(102,102,102,255), 1, true)); //�g�̐F�ݒ�
		//pactiveButton.setBorderPainted(false);

		c.add(leftTurn);
		leftTurn.setBounds(705,390,100,100);
		leftTurn.setFont(new Font("UD �f�W�^�� ���ȏ��� N-B", Font.BOLD, 50));
		leftTurn.setForeground(new Color(192,191,191,255));
		leftTurn.setText("20");

		//�c��^�[����
		resetButton = new JButton(circleIcon);
		c.add(resetButton);
		resetButton.setBounds(680,390,100,100);
		//resetButton.setOpaque(true);
		resetButton.addMouseListener(this);
		resetButton.setContentAreaFilled(false);
		resetButton.setVisible(true);
		resetButton.setBorder(new LineBorder(new Color(102,102,102,255), 1, true)); //�g�̐F�ݒ�
	}

	public void movePointer(int my, int your){

		countSub = my - your; //�R�}�̐��̍����o��
		setDifCounter(countSub); //���ɂ���ăG���f�B���O���ς��

		//�ڑ������� ��l + ����*9
		int des = (165 - countSub*9);
		int dif = (pointerLabel.getY() - des) / 9;

		Timer timer = new Timer(false);
		TimerTask task = new TimerTask() {

			int cnt=0;

			@Override
			public void run() {
				pointerLabel.setLocation(333, pointerLabel.getY() - dif );
				pointcon.setLocation(398, pointcon.getY() - dif );
				cnt++;
				//5����s�Œ�~
				if ( cnt >= 9 ) timer.cancel();
			}
		};
		//�����f�B���C�ƃC���^�[�o��
		timer.schedule(task, 0, 20);

		if(countSub == 0){
			pointcon.setText(" " + Integer.toString(countSub));
		} else if (countSub > 0){
			pointcon.setText("+" + Integer.toString(countSub));
		} else {
			pointcon.setText(Integer.toString(countSub));
		}
		repaint();

	}

	//�ǂ���̃^�[�������������x���̐؂�ւ�
	public void setTurn(){
		if(myTurn == 3){
			//////����̏��� ������Ȃ���ok////////////////////////////////////////////
			if(myIcon.equals(whiteIcon)){
				turnLabel.setIcon(myturnIcon);
				//���Ȃ��̃A�C�R���̂�������
				turnLabel.setBounds(180,540,126,36);
				//����̃K�C�h�\��
				guide();
			} else {
				turnLabel.setIcon(rivalTurnIcon);
				turnLabel.setBounds(30,540,126,36);
			}
		///////////////////////////////////////////////////////////////////////////
		} else {
			//���ڈȍ~�̏���
			Icon whichTurn = turnLabel.getIcon();
			if(whichTurn.equals(myturnIcon)){

				turnLabel.setLocation(180, 540 );//������
				Timer timer = new Timer(false);
				TimerTask task = new TimerTask() {

					int cnt=0;

					@Override
					public void run() {
						turnLabel.setLocation(turnLabel.getX() - 10, 540 );
						cnt++;
						//15����s�Œ�~
						if ( cnt >= 15 ) timer.cancel();
					}
				};
				//�����f�B���C�ƃC���^�[�o��
				timer.schedule(task, 0, 10);
				turnLabel.setIcon(rivalTurnIcon);

			} else {

				turnLabel.setLocation(30, 540 );//������
				Timer timer = new Timer(false);
				TimerTask task = new TimerTask() {

					int cnt=0;

					@Override
					public void run() {
						turnLabel.setLocation(turnLabel.getX() + 10, 540 );
						cnt++;
						//5����s�Œ�~
						if ( cnt >= 15 ) timer.cancel();
					}
				};
				//�����f�B���C�ƃC���^�[�o��
				timer.schedule(task, 0, 10);
				turnLabel.setIcon(myturnIcon);
			}
		}
	}

	public void guide(){
		//������
		guideCount = 0;
		Icon IconComp;
		Icon whichTurn = turnLabel.getIcon();
		if(whichTurn.equals(myturnIcon)){
			for(int i=0; i<8; i++){
				for(int j=0; j<8; j++){
					IconComp = buttonArray[i][j].getIcon();
					if(IconComp == boardIcon){
						//�΂̂Ƃ��T���J�n
						judgeButton2(i,j);
					}
				}
			}
			//for���[�v�I�������ŃK�C�h�̐��𑗐M�A���s����
			String msg = "GUIDE" + " " + guideCount;
			out.println(msg);
			out.flush();
			repaint();
		} else {
			//����̏ꍇ�̓K�C�h�����Z�b�g
			for(int i=0; i<8; i++){
				for(int j=0; j<8; j++){
					//���łɂ���K�C�h�͏���
					IconComp = buttonArray[i][j].getIcon();
					if(IconComp == guideIcon){
						buttonArray[i][j].setIcon(boardIcon);
					}
				}
			}
		}
	}

	public void judgeButton2(int y, int x){
		boolean flag = false;
		//Icon IconComp;
			for(int i=-1; i<=1; i++){
				for(int j=-1; j<=1; j++){
					if(flipButtons(y, x, j, i) >= 1){
						//��ȏ㗠�Ԃ���ꍇ
						flag = true;
						break;
					}
				}
			}
			if(flag){
				guideCount++;
				buttonArray[y][x].setIcon(guideIcon);
			}

	}

	//���̎�O��movePointer�������Ă��I
	//�^�[���̏I���������낢��
	public void endTurn(){
		setPass(); //�p�X
		setTurn(); //�^�[���̃��x���؂�ւ�
		TurnCount++; //�^�[���J�E���g�𑝂₷
		leftTurn.setText(Integer.toString(20 - TurnCount));
		if(20 - TurnCount < 10){
			leftTurn.setBounds(717,390,100,100);
		}
		if(20 - TurnCount < 5){
			leftTurn.setForeground(Color.decode("#902D3E"));
		}
		guide(); //�K�C�h�̍쐬+�u����ꏊ�̔���i�Ȃ���ΏI���j

		//�f�B���C
		try {
			Thread.sleep(250);
		} catch(InterruptedException e){
				e.printStackTrace();
		}
		tellStory(TurnCount); //�X�g�[���[��i�߂�
	}

	//���s����ł�
	public void whichWin(){

			countSub = myIconCount - yourIconCount;
			if(countSub > 0){
				if(getWolfOrRed()){
				//�I�I�J�~
				winnerStr = "wolf";
				} else {
				//����������
				winnerStr = "red";
				}
			} else if(countSub==0){
				winnerStr = "draw";
			} else {
				if(getWolfOrRed()){
				//�I�I�J�~
				winnerStr = "red";
				} else {
				//����������
				winnerStr = "wolf";
				}
			}
	}

	public class SoundPlayer{
			private AudioFormat format = null;
			private DataLine.Info info = null;
			private Clip clip = null;
			boolean stopFlag = false;
			Thread soundThread = null;
			private boolean loopFlag = false;

			public SoundPlayer(String pathname){
					File file = new File(pathname);
					try{
							format = AudioSystem.getAudioFileFormat(file).getFormat();
							info = new DataLine.Info(Clip.class, format);
							clip = (Clip) AudioSystem.getLine(info);
							clip.open(AudioSystem.getAudioInputStream(file));
							//clip.setLoopPoints(0,clip.getFrameLength());//�������[�v�ƂȂ�
					}catch(Exception e){
							e.printStackTrace();
					}
			}

			public void SetLoop(boolean flag){
					loopFlag = flag;
			}

			public void play(){
					soundThread = new Thread(){
							public void run(){
									long time = (long)clip.getFrameLength();//44100�Ŋ���ƍĐ����ԁi�b�j���ł�
									long endTime = System.currentTimeMillis()+time*1000/44100;
									clip.start();
									while(true){
											if(stopFlag){//stopFlag��true�ɂȂ����I��
													clip.stop();
													return;
											}
											if(endTime < System.currentTimeMillis()){//�Ȃ̒������߂�����I��
													if(loopFlag) {
															clip.loop(1);//�������[�v�ƂȂ�
													} else {
															clip.stop();
															return;
													}
											}
											try {
													Thread.sleep(100);
											} catch (InterruptedException e) {
													e.printStackTrace();
											}
									}
							}
					};
					soundThread.start();
			}

			public void stop(){
					stopFlag = true;
			}

	}

	public void actionPerformed(ActionEvent e) {}

	public void testDialog(){
		WinDialogWindow dlg = new WinDialogWindow(this);
		setVisible(true);
	}

	public boolean getWolfOrRed(){
		if(myColor == 0){
			//�I�I�J�~��true
			return true;
		} else {
			//�����������false
			return false;
		}
	}

	//���ݎ����̃^�[���Ȃ�ture��Ԃ��֐�
	public boolean getWhichTurn(){
		Icon whichTurn = turnLabel.getIcon();
		if(whichTurn.equals(myturnIcon)){
			return true;
		} else {
			return false;
		}
	}

	//�t�@�C���p�X�������ɂ���
	public void playSound(String file){
		theSoundPlayer1 = new SoundPlayer(file);
		theSoundPlayer1.SetLoop(false);
		theSoundPlayer1.play();
	}

	public void setDifCounter(int countSub) {
		String msg = "DIF" + " " + countSub;
		out.println(msg);
		out.flush();
		repaint();
	}

	//�A���p�X�̋֎~
	public void setPass(){
		if(passCount > 0) {
			passCount++;
		}
		if(passCount == 4){
			passCount = 0;
		}
	}

	//�X�g�[���[��i�߂�ꏊ�ł�
	public void tellStory(int tCon){

		switch(tCon){
		case 1:

			//���ʕ���
			strow1.setText("�ނ����A����܂ł�");
			strow2.setText("�N���������Ƃ��Ȃ�");
			strow3.setText("�قǂ��ꂢ��");
			strow4.setText("���̎q�����܂����B");

			//���ꂼ��̃R�����g
			if(getWolfOrRed()){
				//�I�I�J�~
				comrow1.setText("�I���̏o�Ԃ͂܂���");
				comrow2.setText("");
			} else {
				//����������
				comrow1.setText("���^�V���l�Ȃ�");
				comrow2.setText("");
			}

			break;
		case 2:
			strow1.setText("���̎q�ɖ����Ȃ��΂�����");
			strow2.setText("�Ԃ����������点�܂�����");
			strow3.setText("���ꂪ�悭���������̂�");
			strow4.setText("�u�Ԃ�����v�ƌĂ΂�܂����B");

			if(getWolfOrRed()){
				//�I�I�J�~
				comrow1.setText("�n�����ւ���");
				comrow2.setText("");
			} else {
				//����������
				comrow1.setText("�z���g��");
				comrow2.setText("�Ȃ܂��̓q�~�c");
			}

			break;
		case 3:
			strow1.setText("������A�ꂪ���̎q�ɂ����܂����B");
			strow2.setText("�u���΂����񂪕a�C������������");
			strow3.setText("�ǂ�ȋ�����Ă����ŁB");
			strow4.setText("�K���b�g�ƃo�^�[�������Ăˁv");

			if(getWolfOrRed()){
				//�I�I�J�~
				comrow1.setText("��������");
				comrow2.setText("�H�����܂���");
			} else {
				//����������
				comrow1.setText("�͂��߂Ă�");
				comrow2.setText("���������");
			}

			break;
		case 4:
			strow1.setText("�Ԃ����񂿂��͕ʂ̑��ɏZ��");
			strow2.setText("���΂�����̏��֌�������");
			strow3.setText("�����ɏo�����܂����B");
			strow4.setText("");

			if(getWolfOrRed()){
				//�I�I�J�~
				comrow1.setText("���悢�悾��");
				comrow2.setText("");
			} else {
				//����������
				comrow1.setText("�����Ă��܁[��");
				comrow2.setText("");
			}

			break;

		//�������番��
		case 5:
			strow1.setText("�Ԃ����񂿂�񂪐X�ɓ����");
			strow2.setText("�I�I�J�~���o�Ă��܂�");
			strow3.setText("�I�I�J�~�͂��̎q��");
			strow4.setText("�H�ׂ����Ȃ�܂���");

			if(getWolfOrRed()){
				//�I�I�J�~
				comrow1.setText("���܂�����");
				comrow2.setText("���X����");
			} else {
				//����������
				comrow1.setText("������ƃN�T�C");
				comrow2.setText("");
			}

			break;

		case 6:
			strow1.setText("�ǂ��֍s���̂�");
			strow2.setText("�Ƃ͂ǂ����ȂǂƂ������");
			strow3.setText("�Ԃ������");
			strow4.setText("����̂܂܂𓚂��܂�");

			if(getWolfOrRed()){
				//�I�I�J�~
				comrow1.setText("�܂������Ȃ���");
				comrow2.setText("");
			} else {
				//����������
				comrow1.setText("�ւ�ȂЂ�");
				comrow2.setText("���A�I�I�J�~��");
			}
			break;

		case 7:
			strow1.setText("�I�I�J�~�͌����܂���");
			strow2.setText("�u�I�����΂�����ɉ�����B");
			strow3.setText("�ǂ�������ɒ�����");
			strow4.setText("�������悤�B�v");

			if(getWolfOrRed()){
				//�I�I�J�~
				comrow1.setText("��ɂ΂������");
				comrow2.setText("�H�ׂĂ��܂���");
			} else {
				//����������
				comrow1.setText("�����݂���");
				comrow2.setText("���͑�����");
			}
			break;

		case 8:
			strow1.setText("�I�I�J�~�͋ߓ���");
			strow2.setText("�����Ă����܂�����");
			strow3.setText("�Ԃ����񂿂���");
			strow4.setText("�V�тȂ���s���܂���");

			if(getWolfOrRed()){
				//�I�I�J�~
				comrow1.setText("���̂���������");
				comrow2.setText("");
			} else {
				//����������
				comrow1.setText("����ȂƂ����");
				comrow2.setText("�L���C�Ȓ���");
			}
			break;

		case 9:
			strow1.setText("�I�I�J�~�͂��΂������");
			strow2.setText("�Ƃɂ��āA");
			strow3.setText("�u���̐Ԃ������v��");
			strow4.setText("��萺�ł����܂����B");

			if(getWolfOrRed()){
				//�I�I�J�~
				comrow1.setText("�m�h���C�^�C��");
				comrow2.setText("");
			} else {
				//����������
				comrow1.setText("����ȂƂ����");
				comrow2.setText("���ꂢ�Ȃ��Ԃ�");
			}
			break;

		case 10:
			strow1.setText("�I�I�J�~�͉Ƃɓ���");
			strow2.setText("���΂�����ɂƂт������");
			strow3.setText("�����H�ׂĂ��܂��܂���");
			strow4.setText("");

			if(getWolfOrRed()){
				//�I�I�J�~
				comrow1.setText("�Ȃ�����");
				comrow2.setText("��������");
			} else {
				//����������
				comrow1.setText("���낻��");
				comrow2.setText("�s����������");
			}
			break;

		case 11:
			strow1.setText("���΂炭�����");
			strow2.setText("�Ԃ����񂿂�񂪗��āA");
			strow3.setText("�˂��������܂��B");
			strow4.setText("");

			if(getWolfOrRed()){
				//�I�I�J�~
				comrow1.setText("�����Ă���");
				comrow2.setText("�A�C�c");
			} else {
				//����������
				comrow1.setText("�����");
				comrow2.setText("������");
			}
			break;

			///////////////////////////////////////////////////////////////

		case 12:
			strow1.setText("�u�ǂȂ����ˁH�v");
			strow2.setText("�@�Ƃ��������������܂��B");
			strow3.setText("�@���΂�����͕��ׂ�");
			strow4.setText("�@�����Ă���̂ł��傤���B");

			if(getWolfOrRed()){
				//�I�I�J�~
				comrow1.setText("����");
				comrow2.setText("�����ė���");
			} else {
				//����������
				comrow1.setText("����Ȑ�");
				comrow2.setText("������������");
			}
			if(difCounter == 0) {

			} else if(difCounter == 2) {
				if(getWolfOrRed()){
					//�I�I�J�~
				} else {
					//����������
					comrow1.setText("..........");
					comrow2.setText("");
				}
			}
			break;

		case 13:
			strow1.setText("�u�Ԃ������B");
			strow2.setText("�@�K���b�g�ƃo�^�[�̚��");
			strow3.setText("�@�����Ă����́v");
			strow4.setText("�@���������Ē��ɓ���܂����B");
			if(difCounter == 0) {

			} else if(difCounter == 2) {
				strow1.setText("����Ȃ͂��͂Ȃ��B");
				strow2.setText("����������͈ٕς�");
				strow3.setText("�C���t���Ă��܂����B");
				strow4.setText("");
			}

			if(getWolfOrRed()){
				//�I�I�J�~
				comrow1.setText("�H�ׂ�̂�");
				comrow2.setText("�I�}�G����");
			} else {
				//����������
				comrow1.setText("�ӂ�񂵂�����");
				comrow2.setText("���v����");
			}
			if(difCounter == 0) {

			} else if(difCounter == 2) {
				if(getWolfOrRed()){
					//�I�I�J�~
					comrow1.setText("�₯��");
					comrow2.setText("�Â�����");
				} else {
					//����������
					comrow1.setText("�ǂ������...");
					comrow2.setText("");
				}
			}

			break;

		case 14:
			strow1.setText("�@�I�I�J�~�̓x�b�h�̉���");
			strow2.setText("�@�����ꂽ�܂܁A");
			strow3.setText("�u�������֗��Ă��΂������");
			strow4.setText("�@�Ƃ��x�݁v�ƌ����܂����B");
			if(difCounter == 0) {

			} else if(difCounter == 2) {
				strow1.setText("�u�X�̒���");
				strow2.setText("�@�Y����̂�������v");
				strow3.setText("�@���������Ă����������");
				strow4.setText("�@�����o���܂����B");
			}

			if(getWolfOrRed()){
				//�I�I�J�~
				comrow1.setText("��������");
				comrow2.setText("");
			} else {
				//����������
				comrow1.setText("�Ȃ񂾂�");
				comrow2.setText("������������...");
			}
			if(difCounter == 0) {

			} else if(difCounter == 2) {
				if(getWolfOrRed()){
					//�I�I�J�~
					comrow1.setText("���Â��ꂽ�H");
					comrow2.setText("");
				} else {
					//����������
					comrow1.setText("��������");
					comrow2.setText("�����܂��傤");
				}
			}
			break;

		case 15:
			strow1.setText("�Ԃ�����͕���E��");
			strow2.setText("�x�b�h�̉��ɓ��낤�Ƃ��܂���");
			strow3.setText("���΂�����̎p������");
			strow4.setText("�ƂĂ������܂��B");
			if(difCounter == 0) {

			} else if(difCounter == 2) {
				strow1.setText("�����������");
				strow2.setText("�t�̂��������");
				strow3.setText("���邱�Ƃɂ��܂����B");
				strow4.setText("");
			}

			if(getWolfOrRed()){
				//�I�I�J�~
				comrow1.setText("��������");
				comrow2.setText("�C���t����邩�H");
			} else {
				//����������
				comrow1.setText("���΂�����...�H");
				comrow2.setText("");
			}
			if(difCounter == 0) {

			} else if(difCounter == 2) {
				if(getWolfOrRed()){
					//�I�I�J�~
					comrow1.setText("���āA");
					comrow2.setText("�ǂ��������̂�");
				} else {
					//����������
					comrow1.setText("���΂�����");
					comrow2.setText("�H�ׂ�ꂽ����");
				}
			}
			break;

		case 16:
			strow1.setText("�u���΂������A");
			strow2.setText("�@�Ȃ�đ傫�Șr�Ȃ́H�v");
			strow3.setText("�u���܂�������");
			strow4.setText("������悤�ɂ���v");
			if(difCounter == 0) {

			} else if(difCounter == 2) {
				strow1.setText("����𕷂���");
				strow2.setText("�t�̂��������");
				strow3.setText("�����������");
				strow4.setText("�ĂѐX�֌������܂��B");
			}

			if(getWolfOrRed()){
				//�I�I�J�~
				comrow1.setText("��������");
				comrow2.setText("���邳�����");
			} else {
				//����������
				comrow1.setText("�Ȃɂ���...");
				comrow2.setText("");
			}
			if(difCounter == 0) {

			} else if(difCounter == 2) {
				if(getWolfOrRed()){
					//�I�I�J�~
					comrow1.setText("�i�l�����j");
					comrow2.setText("");
				} else {
					//����������
					comrow1.setText("����ň��S");
					comrow2.setText("");
				}
			}
			break;

		case 17:
			strow1.setText("�u���΂������A");
			strow2.setText("�@�Ȃ�đ傫�ȋr�Ȃ́H�v");
			strow3.setText("�u���������悤�ɂ���v");
			strow4.setText("");
			if(difCounter == 0) {

			} else if(difCounter == 2) {
				strow1.setText("�����ۂ��A");
				strow2.setText("�I�I�J�~���^����");
				strow3.setText("���邱�ƂɋC���t����");
				strow4.setText("���܂����B");
			}

			if(getWolfOrRed()){
				//�I�I�J�~
				comrow1.setText("��������");
				comrow2.setText("");
			} else {
				//����������
				comrow1.setText(".........");
				comrow2.setText("");
			}
			if(difCounter == 0) {

			} else if(difCounter == 2) {
				if(getWolfOrRed()){
					//�I�I�J�~
					comrow1.setText("������͂Ȃ���");
					comrow2.setText("");
				} else {
					//����������
					comrow1.setText("���������Ȃ���");
					comrow2.setText("");
				}
			}
			break;

		case 18:
			strow1.setText("�u���΂������A");
			strow2.setText("�@�Ȃ�đ傫�Ȏ��Ȃ́H�v");
			strow3.setText("�u�悭��������悤�ɂ���v");
			strow4.setText("");
			if(difCounter == 0) {

			} else if(difCounter == 2) {
				strow1.setText("�I�I�J�~��");
				strow2.setText("�x�b�h�Ƃ͈Ⴄ�ꏊ��");
				strow3.setText("�B��邱�Ƃɂ��܂����B");
				strow4.setText("");
			}

			if(getWolfOrRed()){
				//�I�I�J�~
				comrow1.setText("���Ə���...");
				comrow2.setText("");
			} else {
				//����������
				comrow1.setText("�����Ȃ̂�����");
				comrow2.setText("");
			}
			if(difCounter == 0) {

			} else if(difCounter == 2) {
				if(getWolfOrRed()){
					//�I�I�J�~
					comrow1.setText("�����Ȃ�");
					comrow2.setText("�C�Â���Ȃ���");
				} else {
					//����������
					comrow1.setText("����������");
					comrow2.setText("");
				}
			}
			break;

		case 19:
			strow1.setText("�u���΂������A");
			strow2.setText("�@�Ȃ�đ傫�ȖڂȂ́H�v");
			strow3.setText("�u�悭������悤�ɂ���v");
			strow4.setText("");
			if(difCounter == 0) {

			} else if(difCounter == 2) {
				strow1.setText("�߂��Ă������������񂽂��B");
				strow2.setText("�Ƃɓ���܂������A�I�I�J�~��");
				strow3.setText("�p�͂ǂ��ɂ�����܂���B");
				strow4.setText("");
			}

			if(getWolfOrRed()){
				//�I�I�J�~
				comrow1.setText("�悾�ꂪ...");
				comrow2.setText("");
			} else {
				//����������
				comrow1.setText("����ς�");
				comrow2.setText("�����ς���");
			}
			if(difCounter == 0) {

			} else if(difCounter == 2) {
				if(getWolfOrRed()){
					//�I�I�J�~
					comrow1.setText("........");
					comrow2.setText("");
				} else {
					//����������
					comrow1.setText("�����������...");
					comrow2.setText("");
				}
			}
			break;

		case 20:
			strow1.setText("�u���΂������A");
			strow2.setText("�@�Ȃ�đ傫�Ȏ��Ȃ́H�v");
			strow3.setText("�u���܂���H�ׂ邽�߂��v");
			strow4.setText("");
			if(difCounter == 0) {
				strow1.setText("�u���΂������A");
				strow2.setText("�@�Ȃ�đ傫�Ȏ��Ȃ́H�v");
				strow3.setText("�u�܂āA�N�͒N�����H�v");
				strow4.setText("");
			} else if(difCounter == 2) {
				strow1.setText("�s�R�Ɏv������l��");
				strow2.setText("�O�֏o�����̎�...");
				strow3.setText("");
				strow4.setText("");
			}

			if(getWolfOrRed()){
				//�I�I�J�~
				comrow1.setText("");
				comrow2.setText("");
			} else {
				//����������
				comrow1.setText("�I�I�J�~�I");
				comrow2.setText("");
			}
			if(difCounter == 0) {
				if(getWolfOrRed()){
					//�I�I�J�~
					comrow1.setText("");
					comrow2.setText("");
				} else {
					//����������
					comrow1.setText("");
					comrow2.setText("");
				}
			} else if(difCounter == 2) {
				if(getWolfOrRed()){
					//�I�I�J�~
					comrow1.setText("");
					comrow2.setText("");
				} else {
					//����������
					comrow1.setText("");
					comrow2.setText("");
				}
			}

			//�f�B���C
			try {
				Thread.sleep(1500);
			} catch(InterruptedException e){
					e.printStackTrace();
			}

			//�Ō�̂Ƃ���͔����ďI��������
			//�I������
			if(getWhichTurn()){
				String msg = "JUDGE";
				//�T�[�o�ɏ��𑗂�
				out.println(msg);
				out.flush();
				repaint();
			}
			break;

		}
	}

}

//�_�C�A���O�p�̃N���X
class WinDialogWindow extends JDialog implements MouseListener,ActionListener{
	ImageIcon endbtnIcon = new ImageIcon("icons/endbtn.png");
	ImageIcon endactIcon = new ImageIcon("icons/eactive.png");
	ImageIcon opStartIcon = new ImageIcon("icons/start.png");
	ImageIcon opStartActIcon = new ImageIcon("icons/start_act.png");
	ImageIcon opEndIcon = new ImageIcon("icons/end.png");
	ImageIcon opEndActIcon = new ImageIcon("icons/end_act.png");
	ImageIcon opBackIcon = new ImageIcon("icons/op_back.png");
	JButton opStartbtn = new JButton(opStartIcon);
	JButton opStartActbtn = new JButton(opStartActIcon);
	JButton opEndbtn = new JButton(opEndIcon);
	JButton opEndActbtn = new JButton(opEndActIcon);
	JButton endbtn = new JButton(endbtnIcon);
	JButton eactivebtn = new JButton(endactIcon);

		WinDialogWindow(JFrame owner) {
			super(owner);
			Container c = this.getContentPane();
			c.setLayout(null);
			//�G���f�B���O�����ɕK�v�ȉ摜

			ImageIcon wolf_win = new ImageIcon("icons/wolf_win.png");
			ImageIcon red_win = new ImageIcon("icons/red_win.png");
			ImageIcon draw = new ImageIcon("icons/draw.png");
			ImageIcon wolf_winwin = new ImageIcon("icons/wolf_winwin.png");
			ImageIcon red_winwin = new ImageIcon("icons/red_winwin.png");
			ImageIcon drawdraw  = new ImageIcon("icons/drawdraw.png");
			JLabel main_image = new JLabel();

			//�Ó]������
			littleRed.back90.setVisible(true);

			//�f�B���C
			try {
				Thread.sleep(500);
			} catch(InterruptedException e){
					e.printStackTrace();
			}

			//7�̃G���f�B���O�؂�ւ�
			if(littleRed.difCounter == 3) {
				littleRed.back90.setVisible(false);
				main_image.setIcon(opBackIcon);
			} else if(littleRed.TurnCount >= 20 && littleRed.difCounter == 0){
				//drawdraw
				main_image.setIcon(drawdraw);
			} else if(littleRed.difCounter == 1){
				//wolfwin, redwin, draw
				if(littleRed.TurnCount >= 20 && littleRed.winnerStr.equals("wolf")){
					//wolf_win
					main_image.setIcon(wolf_win);
					//redwin
				} else if(littleRed.winnerStr.equals("red")) {
					main_image.setIcon(red_win);
				} else {
					//draw
					main_image.setIcon(draw);
				}
			} else if(littleRed.TurnCount >= 20 && littleRed.difCounter == 2){
				//wolfwinwin, redwinwin
				if(littleRed.winnerStr.equals("wolf")){
					main_image.setIcon(wolf_winwin);
				} else if(littleRed.winnerStr.equals("red")) {
					main_image.setIcon(red_winwin);
				} else {
					main_image.setIcon(draw);
				}

			} else {
				//���U�Ō����������ꍇ
				main_image.setIcon(draw);
			}

			if(littleRed.difCounter != 3){
			//�I���̃{�^��
			c.add(endbtn);
			endbtn.setBounds(490,290,100,100);
			endbtn.setOpaque(true);
			endbtn.addMouseListener(this);
			endbtn.setContentAreaFilled(false);
			endbtn.setVisible(true);
			endbtn.setBorder(new LineBorder(new Color(102,102,102,255), 1, true)); //�g�̐F�ݒ�

			eactivebtn.addActionListener(this);
			c.add(eactivebtn);
			eactivebtn.setBounds(490,290,100,100);
			eactivebtn.setOpaque(true);
			eactivebtn.addMouseListener(this);
			eactivebtn.setContentAreaFilled(false);
			eactivebtn.setVisible(false);
			eactivebtn.setBorder(new LineBorder(new Color(102,102,102,255), 1, true)); //�g�̐F�ݒ�

			c.add(main_image);
			main_image.setBounds(0,0,600,400);

			setTitle("");
			setResizable(false);
			setUndecorated(true);
			setSize(600, 400);

		} else {

			c.add(opStartbtn);
			opStartbtn.setBounds(200,340,350,80);
			opStartbtn.setOpaque(true);
			opStartbtn.addMouseListener(this);
			opStartbtn.setContentAreaFilled(false);
			opStartbtn.setVisible(true);
			opStartbtn.setBorderPainted(false);

			opStartActbtn.addActionListener(this);
			c.add(opStartActbtn);
			opStartActbtn.setBounds(200,340,350,80);
			opStartActbtn.setOpaque(true);
			opStartActbtn.addMouseListener(this);
			opStartActbtn.setContentAreaFilled(false);
			opStartActbtn.setVisible(false);
			opStartActbtn.setBorderPainted(false);
			opStartActbtn.setActionCommand("start");

			c.add(opEndbtn);
			opEndbtn.setBounds(200,440,350,80);
			opEndbtn.setOpaque(true);
			opEndbtn.addMouseListener(this);
			opEndbtn.setContentAreaFilled(false);
			opEndbtn.setVisible(true);
			opEndbtn.setBorderPainted(false);

			opEndActbtn.addActionListener(this);
			c.add(opEndActbtn);
			opEndActbtn.setBounds(200,440,350,80);
			opEndActbtn.setOpaque(true);
			opEndActbtn.addMouseListener(this);
			opEndActbtn.setContentAreaFilled(false);
			opEndActbtn.setVisible(false);
			opEndActbtn.setBorderPainted(false);
			opEndActbtn.setActionCommand("end");

			c.add(main_image);
			main_image.setBounds(0,0,800,600);

			setResizable(true);
			setUndecorated(false);
			setTitle("Little Red");
			setSize(800, 600);

		}

			setModal(true);//������܂ŉ���G��Ȃ�����ifalse�ɂ���ƐG���j

			//�_�C�A���O�̑傫����\���ꏊ��ύX�ł���
			//�e�̃_�C�A���O�̒��S�ɕ\���������ꍇ�́C�e�̃E�B���h�E�̒��S���W�����߂āC�q�̃_�C�A���O�̑傫���̔������炷
			setLocation(owner.getBounds().x+owner.getWidth()/2-this.getWidth()/2,owner.getBounds().y+owner.getHeight()/2-this.getHeight()/2);
			setVisible(true);
		}

		//�{�^���������ꂽ�Ƃ��̏���
    public void actionPerformed(ActionEvent e) {
			String str = e.getActionCommand();
			if(str.equals("start")){
				littleRed.op = 0;
			} else if(str.equals("end")){
				littleRed.op = 1;
			}
			this.dispose();//Dialog��p������
		}

		public void mouseEntered(MouseEvent e) {
			JButton theButton = (JButton)e.getComponent();
			Icon theIcon = theButton.getIcon();
			//�A�C�R���̂��肩��
			if(theIcon.equals(endbtnIcon)){
				endbtn.setVisible(false);
				eactivebtn.setVisible(true);
			} else if(theIcon.equals(opStartIcon)){
				opStartbtn.setVisible(false);
				opStartActbtn.setVisible(true);
			} else if(theIcon.equals(opEndIcon)) {
				opEndbtn.setVisible(false);
				opEndActbtn.setVisible(true);
			}
		}

		public void mouseExited(MouseEvent e) {
			JButton theButton = (JButton)e.getComponent();
			Icon theIcon = theButton.getIcon();
			//�A�C�R���̐؂�ւ�
			if(theIcon.equals(endactIcon)){
				endbtn.setVisible(true);
				eactivebtn.setVisible(false);
			} else if(theIcon.equals(opStartActIcon)) {
				opStartbtn.setVisible(true);
				opStartActbtn.setVisible(false);
			} else if(theIcon.equals(opEndActIcon)) {
				opEndbtn.setVisible(true);
				opEndActbtn.setVisible(false);
			}
		}

		public void mouseClicked(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e){}
		public void mouseDragged(MouseEvent e) {}
		public void mouseMoved(MouseEvent e) {}

}
