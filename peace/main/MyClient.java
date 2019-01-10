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

public class MyClient extends JFrame implements MouseListener,MouseMotionListener,ActionListener {
	private JButton buttonArray[][];//�{�^���p�̔z��
	private Container c;
	private ImageIcon whiteIcon, redIcon, boardIcon,pactiveIcon,ractiveIcon,
			passIcon, resetIcon, guideIcon, redHoodIcon, wolfIcon, wmicon, rmicon;
	private int myColor;
	private int myTurn = 3; //myTurn==3�̂Ƃ������^�[���Ƃ���BmyTurn==0�͍��A1�͔��B
	private ImageIcon myIcon, yourIcon;
	private int flipNum = 0;
	private int TurnCount = 0;
	PrintWriter out;//�o�͗p�̃��C�^�[
	private JButton passButton, resetButton, pactiveButton, ractiveButton;
	private int myIconCount = 2, yourIconCount = 2, countSub =0;
	private String chara = "";
	int guideCount = 0;
	static int winner = 2; //0�̂Ƃ�����
	SoundPlayer theSoundPlayer1;//�ǂ�����ł��A�N�Z�X�ł���悤�ɁC�N���X�̃����o�Ƃ��Đ錾

	//�|�C���^�[�̃A�C�R��
	ImageIcon pointerIcon = new ImageIcon("icons/arrow.png");
	JLabel pointerLabel = new JLabel(pointerIcon);

	//�|�C���^�[�̃J�E���g
	JLabel pointcon = new JLabel();

	//�^�[���������A�C�R��
	ImageIcon myturnIcon = new ImageIcon("icons/yourturn.png");
	ImageIcon rivalTurnIcon = new ImageIcon("icons/rivalturn.png");
	JLabel imturnLabel = new JLabel(myturnIcon);

	//���O
	JLabel strow1 = new JLabel();
	JLabel strow2 = new JLabel();
	JLabel strow3 = new JLabel();
	JLabel strow4 = new JLabel();

	JLabel comrow1 = new JLabel();
	JLabel comrow2 = new JLabel();

	public MyClient() {

		/*
		//���O�̓��̓_�C�A���O���J��
		String myName = JOptionPane.showInputDialog(null,"���O����͂��Ă�������","���O�̓���",JOptionPane.QUESTION_MESSAGE);
		if(myName.equals("")){
			myName = "No name";//���O���Ȃ��Ƃ��́C"No name"�Ƃ���
		}
		*/

		String myName = "No name";

		//IP�A�h���X�̓���
		String myIp = JOptionPane.showInputDialog(null,"IP�A�h���X����͂��Ă�������","IP�A�h���X�̓���",JOptionPane.QUESTION_MESSAGE);
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

				c.add(imturnLabel);
				imturnLabel.setBounds(180,540,126,36);
				//imturnLabel.setOpaque(true);
				imturnLabel.setIcon(rivalTurnIcon);

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

				} else {
					myColor = 1;
					myIcon = redIcon;
					yourIcon = whiteIcon;
					setTurn();

					//���Ȃ��͂���������
					redHoodLab.setBounds(180,410,125,125);
					redminilab.setBounds(365,10,50,50);

					//���Ȃ��̓I�I�J�~
					wolfLab.setBounds(30,410,125,125);
					wolfminiLab.setBounds(365,340,50,50);

				}
				//�w�i�摜�͍Ō�ɒ�`����
				ImageIcon mainImIcon = new ImageIcon("icons/main-frame.jpg");
				JLabel mainImLabel = new JLabel(mainImIcon);
				c.add(mainImLabel);
				mainImLabel.setBounds(0,0,800,600);
				//mainImLabel.setOpaque(true);

				while(true) {
					String inputLine = br.readLine();//�f�[�^����s�������ǂݍ���ł݂�
					if (inputLine != null) {//�ǂݍ��񂾂Ƃ��Ƀf�[�^���ǂݍ��܂ꂽ���ǂ������`�F�b�N����
						System.out.println(inputLine);//�f�o�b�O�i����m�F�p�j�ɃR���\�[���ɏo�͂���
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

						} else if(cmd.equals("RESET")) {
							//���Z�b�g�{�^���������ꂽ�Ƃ��̏���
							
							//�_�C�A���O�̃e�X�g
							testDialog();
						} else if(cmd.equals("JUDGE")){
							int win = Integer.parseInt(inputTokens[1]);//guideCount
							testDialog();
							
						} else if(cmd.equals("GUIDE")){
							int theGuide = Integer.parseInt(inputTokens[1]);//guideCount
							guideCount = theGuide; //�K�C�h�̐��𗼕��ɓK���A���L����
							//System.out.println("guideCount = " + guideCount);

							//�u���Ȃ���Ώ��s����
							whichWin();
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
		MyClient net = new MyClient();
		net.setVisible(true);
	}

	public void mouseClicked(MouseEvent e) {//�{�^�����N���b�N�����Ƃ��̏���
		System.out.println("�N���b�N���܂���"); //�f�o�b�N
		JButton theButton = (JButton)e.getComponent();//�N���b�N�����I�u�W�F�N�g�𓾂�D�^���Ⴄ�̂ŃL���X�g����
		Icon theIcon = theButton.getIcon();//theIcon�ɂ́C���݂̃{�^���ɐݒ肳�ꂽ�A�C�R��������

		if(theIcon.equals(boardIcon) || theIcon.equals(guideIcon)){
			String theArrayIndex = theButton.getActionCommand();//�{�^���̔z��̔ԍ������o��
			int temp = Integer.parseInt(theArrayIndex);
			int tempx = temp / 8;
			int tempy = temp % 8;
			//System.out.println("tempx = " + tempx + "tempy = " + tempy);
			if(judgeButton(tempy, tempx)){
				//������
				Icon whichTurn = imturnLabel.getIcon();
				if(whichTurn.equals(myturnIcon)){
					String msg = "PLACE" + " " + theArrayIndex + " " + myColor + " " + theIcon;
					//�T�[�o�ɏ��𑗂�
					out.println(msg);
					out.flush();
					repaint();
				} else {
					System.out.println("����̃^�[���ɂ͒u���܂���");
				}

			} else {
				//�u���Ȃ�
			}
			repaint();//��ʂ̃I�u�W�F�N�g��`�悵����

		} else if(theIcon.equals(pactiveIcon)){ //pass�ł͂Ȃ�pactive�ɒ���
				Icon whichTurn = imturnLabel.getIcon();
				String msg = "PASS" + " " + myTurn + " " + myColor;
				//�T�[�o�ɏ��𑗂�
				if(whichTurn.equals(myturnIcon)){
				out.println(msg);
				out.flush();
				repaint();
				} else {
					System.out.println("����̃^�[���ɂ̓p�X�ł��܂���");
				}

		} else if(theIcon.equals(ractiveIcon)){
				String msg = "RESET"+ " " + myColor;
				//�T�[�o�ɏ��𑗂�
				out.println(msg);
				out.flush();
				repaint();

				/*
				////////////////�T�E���h�̃e�X�g/////////////////////////////////////////
				theSoundPlayer1 = new SoundPlayer("sounds/kot.wav");
				theSoundPlayer1.SetLoop(false);//�a�f�l�Ƃ��čĐ����J��Ԃ�
				theSoundPlayer1.play();
				///////////////////////////////////////////////////////////////////////

				///////////timer�̃e�X�g////////////////////////////////////////////////
				Timer timer = new Timer(false);
				TimerTask task = new TimerTask() {

					int cnt=0;

					@Override
					public void run() {
						System.out.println("�Ă���");
						cnt++;
						//5����s�Œ�~
						if ( cnt >= 5 ) timer.cancel();
					}
				};
				timer.schedule(task, 0, 1000);
				////////////////////////////////////////////////////////////////////////
				*/

		}
	}

	public void mouseEntered(MouseEvent e) {//�}�E�X���I�u�W�F�N�g�ɓ������Ƃ��̏���
		//System.out.println("�}�E�X��������");

		JButton theButton = (JButton)e.getComponent();//�N���b�N�����I�u�W�F�N�g�𓾂�D�^���Ⴄ�̂ŃL���X�g����
		Icon theIcon = theButton.getIcon();//theIcon�ɂ́C���݂̃{�^���ɐݒ肳�ꂽ�A�C�R��������

		if(theIcon.equals(boardIcon) || theIcon.equals(guideIcon) || theIcon.equals(redIcon) ||theIcon.equals(whiteIcon)){

		///////////////////////////////////////////////////////////////////////
		theSoundPlayer1 = new SoundPlayer("sounds/kot.wav");
		theSoundPlayer1.SetLoop(false);//�a�f�l�Ƃ��čĐ����J��Ԃ�
		theSoundPlayer1.play();

		///////////////////////////////////////////////////////////////////////
		} else	if (theIcon.equals(passIcon)){
			//�d�˂Ă����ĕ\���E��\����؂�ւ���
			passButton.setVisible(false);
			pactiveButton.setVisible(true);
		} else if (theIcon.equals(resetIcon)){
			//�d�˂Ă����ĕ\���E��\����؂�ւ���
			resetButton.setVisible(false);
			ractiveButton.setVisible(true);
		}
	}

	public void mouseExited(MouseEvent e) {//�}�E�X���I�u�W�F�N�g����o���Ƃ��̏���
		//System.out.println("�}�E�X�E�o");

		JButton theButton = (JButton)e.getComponent();//�N���b�N�����I�u�W�F�N�g�𓾂�D�^���Ⴄ�̂ŃL���X�g����
		Icon theIcon = theButton.getIcon();//theIcon�ɂ́C���݂̃{�^���ɐݒ肳�ꂽ�A�C�R��������

		if(theIcon.equals(boardIcon) || theIcon.equals(guideIcon) || theIcon.equals(redIcon) ||theIcon.equals(whiteIcon)){

		} else	if (theIcon.equals(pactiveIcon)){
			//System.out.println("�o��");
			passButton.setVisible(true);
			pactiveButton.setVisible(false);
		} else if (theIcon.equals(ractiveIcon)){
			//System.out.println("�o��");
			resetButton.setVisible(true);
			ractiveButton.setVisible(false);
		}
	}

	public void mousePressed(MouseEvent e) {//�}�E�X�ŃI�u�W�F�N�g���������Ƃ��̏����i�N���b�N�Ƃ̈Ⴂ�ɒ��Ӂj
		//System.out.println("�}�E�X��������");
	}

	public void mouseReleased(MouseEvent e) {//�}�E�X�ŉ����Ă����I�u�W�F�N�g�𗣂����Ƃ��̏���
		//System.out.println("�}�E�X�������");
	}

	public void mouseDragged(MouseEvent e) {//�}�E�X�ŃI�u�W�F�N�g�Ƃ��h���b�O���Ă���Ƃ��̏���
	}

	public void mouseMoved(MouseEvent e) {//�}�E�X���I�u�W�F�N�g��ňړ������Ƃ��̏���
	}

	public boolean judgeButton(int y, int x){
		//System.out.println("judgeButton���Ă΂�܂���"); //�f�o�b�N
		boolean flag = false;
		Icon IconComp;
			for(int i=-1; i<=1; i++){
				for(int j=-1; j<=1; j++){
					if(flipButtons(y, x, j, i) >= 1){ //��ȏ㗠�Ԃ���ꍇ
						IconComp = buttonArray[y+j][x+i].getIcon();
						//System.out.println("y+j="+(y+j)+", x+i="+(x+i));
						flag = true;

						for(int dy=j, dx=i, k=0; k<flipNum; k++, dy+=j, dx+=i){
							//�{�^���̈ʒu�������
							int msgy = y + dy;
							int msgx = x + dx;
							int theArrayIndex = msgx*8 + msgy;
							Icon whichTurn = imturnLabel.getIcon();

							//�����̃^�[���̂Ƃ������t���b�v���M
							if(whichTurn.equals(myturnIcon)){
								String msg = "FLIP"+" "+theArrayIndex+" "+myColor;
								out.println(msg);
								out.flush();
								repaint();
							} else {
								System.out.println("����̃^�[���ɂ͗��Ԃ��܂���");
							}
						}
					} else { //�ЂƂ����Ԃ��Ȃ�
						//System.out.println("�ЂƂ����Ԃ��Ȃ�"); //�f�o�b�N
					}
				}
			}
			return flag;
	}

	public int flipButtons(int y, int x, int j, int i){
		//System.out.println("flipButtons���Ă΂�܂���"); //�f�o�b�N
		Icon IconRev;
		flipNum = 0; //������
		for(int dy=j, dx=i; ; dy+=j, dx+=i) { //�I����������ł�
			try{
				IconRev = buttonArray[(y+dy)][(x+dx)].getIcon();
				//System.out.println("y+dy = " + (y+dy) + " " + "x+dx = " + (x+dx)); //�f�o�b�N
				//System.out.println("IconRev = " + IconRev); //�f�o�b�N
				if(dy == 0){
					if(dx == 0){
						flipNum = 0;
						break;
					}
				}

				if(IconRev.equals(boardIcon)){
				//System.out.println("���̕����ɂ͗΂������");
				flipNum = 0;
				break;
				} else if(IconRev.equals(myIcon)) {
					//System.out.println("���̕����ɂ͌N�̐F�������");
					break;
				} else if(IconRev.equals(yourIcon)){
					//System.out.println("�܂��i�߂��");
					flipNum++;
				} else if(IconRev.equals(guideIcon)){
					flipNum = 0;
					break;
				}
			}catch(ArrayIndexOutOfBoundsException e){
				//System.out.println("���̕����ɂ͔Ֆʂ�����܂���");
				flipNum = 0;
				break;
			}
		}
		return flipNum;
	}

	public static void setFlame() {
		JFrame frame = new JFrame();
		// �^�C�g������ݒ�
		frame.setTitle( "�w�i�F��ԐF��" );
		// �t���[���̑傫����ݒ�
		frame.setSize( 400, 320 );
		// �h�~�h�{�^�������������̏�����ݒ�
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		// �w�i�F�̕ύX
		frame.getContentPane().setBackground( Color.RED );
		// �t���[����\��
		frame.setVisible( true );
	}

	///////////////////////////////////////////UI�̐���////////////////////////////
	public void setUp(){
		//�E�B���h�E���쐬����
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//�E�B���h�E�����Ƃ��ɁC����������悤�ɐݒ肷��
		setTitle("MyClient");//�E�B���h�E�̃^�C�g����ݒ肷��
		setSize(812,635);//�E�B���h�E�̃T�C�Y��ݒ肷��
		//getContentPane().setBackground( Color.decode("#685653")); //�w�i�F�̕ύX
		c = getContentPane();//�t���[���̃y�C�����擾����

		//�A�C�R���̐ݒ�
		redIcon = new ImageIcon("icons/r-icon.png");
		whiteIcon = new ImageIcon("icons/w-icon.png");
		boardIcon = new ImageIcon("icons/b-icon.png");
		passIcon = new ImageIcon("icons/pass.png");
		pactiveIcon = new ImageIcon("icons/pactive.png");
		resetIcon = new ImageIcon("icons/reset.png");
		ractiveIcon= new ImageIcon("icons/reset-active.png");
		guideIcon = new ImageIcon("icons/g-icon.png");

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
			//buttonArray[i][j].setBorderPainted(false); //���̓�����
		  }
		}

		buttonArray[3][3].setIcon(redIcon);
		buttonArray[4][3].setIcon(whiteIcon);
		buttonArray[3][4].setIcon(whiteIcon);
		buttonArray[4][4].setIcon(redIcon);

		//�|�C���^�[�J�E���g �ق�����g���̂ŊO�Ő錾
		c.add(pointcon);
		pointcon.setBounds(398,180,30,30);
		pointcon.setText(" " + Integer.toString(countSub));
		pointcon.setForeground(Color.decode("#c0bfbf"));
		//pointcon.setOpaque(true); //�w�i������
		pointcon.setFont(new Font("UD �f�W�^�� ���ȏ��� N-B", Font.PLAIN, 18));

		//�|�C���^�[ �ق�����g���̂ŊO�Ő錾
		c.add(pointerLabel);
		pointerLabel.setBounds(360-40+13,200-70+35,60,60);
		//pointerLabel.setOpaque(true);

		//���O�̒��g �ق��ł��g���̂Ő擪�Œ�`�B
		//JLabel strow1 = new JLabel();
		c.add(strow1);
		strow1.setBounds(20,-10,300,300);
		strow1.setText("�����̃e�L�X�g�ł��B�����̃e�L�X�g�B");
		strow1.setFont(new Font("UD �f�W�^�� ���ȏ��� N-B", Font.PLAIN, 16));
		strow1.setForeground(new Color(192,191,191,255));

		//JLabel strow2 = new JLabel();
		c.add(strow2);
		strow2.setBounds(20,50,300,300);
		strow2.setText("�����̃e�L�X�g�ł��B�����̃e�L�X�g�B");
		strow2.setFont(new Font("UD �f�W�^�� ���ȏ��� N-B", Font.PLAIN, 16));
		strow2.setForeground(new Color(192,191,191,255));

		//JLabel strow3 = new JLabel();
		c.add(strow3);
		strow3.setBounds(20,110,300,300);
		strow3.setText("�����̃e�L�X�g�ł��B�����̃e�L�X�g�B");
		strow3.setFont(new Font("UD �f�W�^�� ���ȏ��� N-B", Font.PLAIN, 16));
		strow3.setForeground(new Color(192,191,191,255));

		//JLabel strow4 = new JLabel();
		//������MAX15����
		c.add(strow4);
		strow4.setBounds(20,170,300,300);
		strow4.setText("�����̃e�L�X�g�ł��B�����B");
		strow4.setFont(new Font("UD �f�W�^�� ���ȏ��� N-B", Font.PLAIN, 16));
		strow4.setForeground(new Color(192,191,191,255));

		c.add(comrow1);
		comrow1.setBounds(388,412,200,100);
		comrow1.setText("���߂�Ƃ���");
		comrow1.setFont(new Font("UD �f�W�^�� ���ȏ��� N-B", Font.PLAIN, 16));
		comrow1.setForeground(new Color(192,191,191,255));

		c.add(comrow2);
		comrow2.setBounds(388,452,200,100);
		comrow2.setText("�ɂ��傤�߂���");
		comrow2.setFont(new Font("UD �f�W�^�� ���ȏ��� N-B", Font.PLAIN, 16));
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
		//passButton.setBorderPainted(false);

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

		//reset�{�^��
		resetButton = new JButton(resetIcon);
		c.add(resetButton);
		resetButton.setBounds(680,390,100,100);
		resetButton.setOpaque(true);
		resetButton.addMouseListener(this);
		resetButton.setContentAreaFilled(false);
		resetButton.setVisible(true);
		resetButton.setBorder(new LineBorder(new Color(102,102,102,255), 1, true)); //�g�̐F�ݒ�
		//resetButton.setBorderPainted(false);

		//�A�N�e�B�u���
		ractiveButton = new JButton(ractiveIcon);
		c.add(ractiveButton);
		ractiveButton.setBounds(680,390,100,100);
		ractiveButton.setOpaque(true);
		ractiveButton.addMouseListener(this);
		ractiveButton.setContentAreaFilled(false);
		ractiveButton.setVisible(false);
		ractiveButton.setBorder(new LineBorder(new Color(102,102,102,255), 1, true)); //�g�̐F�ݒ�
		//resetButton.setBorderPainted(false);
		
		//�_�C�A���O�̃e�X�g
		//ractiveButton.setActionCommand("PUSH_Dialog");//�{�^�����N���b�N�����Ƃ���actionPerformed��theCmd�Ŏ󂯂Ƃ镶����
        //ractiveButton.addActionListener(this);//�{�^�����N���b�N�����Ƃ���actionPerformed�Ŏ󂯎�邽��

	}
	//////////////////////////////////////////////////////////////////////////////

	//���Z�b�g�̏���
	public void resetAll(){
		for(int j=0;j<8;j++){
			for(int i=0;i<8;i++){
			buttonArray[i][j].setIcon(boardIcon);
			}
		}

		buttonArray[3][3].setIcon(redIcon);
		buttonArray[4][3].setIcon(whiteIcon);
		buttonArray[3][4].setIcon(whiteIcon);
		buttonArray[4][4].setIcon(redIcon);

	}

	public void movePointer(int my, int your){

		countSub = my - your; //�����������Ɓ{
		System.out.println("///////countsub = ////////" + countSub);

		//�ڑ������� ��l + ����*9
		int des = (165 - countSub*9);
		//System.out.println("///////des = ////////" + des);
		int dif = (pointerLabel.getY() - des) / 9;
		//System.out.println("///////dif = ////////" + dif);

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

	public void setTurn(){
		if(myTurn == 3){
			//////����̏��� ������Ȃ���ok////////////////////////////////////////////
			if(myIcon.equals(whiteIcon)){
				imturnLabel.setIcon(myturnIcon);
				//���Ȃ��̃A�C�R���̂�������
				imturnLabel.setBounds(180,540,126,36);
				//����̃K�C�h�\��
				guide();
			} else {
				imturnLabel.setIcon(rivalTurnIcon);
				imturnLabel.setBounds(30,540,126,36);
			}
		///////////////////////////////////////////////////////////////////////////
		} else {
			//���ڈȍ~�̏���
			Icon whichTurn = imturnLabel.getIcon();
			//System.out.println(whichTurn);
			if(whichTurn.equals(myturnIcon)){

				imturnLabel.setLocation(180, 540 );//������
				Timer timer = new Timer(false);
				TimerTask task = new TimerTask() {

					int cnt=0;

					@Override
					public void run() {
						imturnLabel.setLocation(imturnLabel.getX() - 3, 540 );
						cnt++;
						//5����s�Œ�~
						if ( cnt >= 50 ) timer.cancel();
					}
				};
				//�����f�B���C�ƃC���^�[�o��
				timer.schedule(task, 0, 1);
				imturnLabel.setIcon(rivalTurnIcon);

			} else {

				imturnLabel.setLocation(30, 540 );//������
				Timer timer = new Timer(false);
				TimerTask task = new TimerTask() {

					int cnt=0;

					@Override
					public void run() {
						imturnLabel.setLocation(imturnLabel.getX() + 3, 540 );
						cnt++;
						//5����s�Œ�~
						if ( cnt >= 50 ) timer.cancel();
					}
				};
				//�����f�B���C�ƃC���^�[�o��
				timer.schedule(task, 0, 1);
				imturnLabel.setIcon(myturnIcon);
			}
		}
	}

	public void guide(){
		//System.out.println("guide();���Ă΂ꂽ");
		//������
		guideCount = 0;
		Icon IconComp;
		Icon whichTurn = imturnLabel.getIcon();
		if(whichTurn.equals(myturnIcon)){
			for(int i=0; i<8; i++){
				for(int j=0; j<8; j++){
					IconComp = buttonArray[i][j].getIcon();
					//System.out.println(IconComp);
					if(IconComp == boardIcon){
						//�΂̂Ƃ��T���J�n
						//System.out.println("�W���b�W�J�n");
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
						//System.out.println("�K�C�h������");
						buttonArray[i][j].setIcon(boardIcon);
					}
				}
			}
		}
	}

	public void judgeButton2(int y, int x){
		//System.out.println("judgeButton2���Ă΂�܂���"); //�f�o�b�N
		boolean flag = false;
		//Icon IconComp;
			for(int i=-1; i<=1; i++){
				for(int j=-1; j<=1; j++){
					if(flipButtons(y, x, j, i) >= 1){ //��ȏ㗠�Ԃ���ꍇ
						//System.out.println("flipNum" + flipNum);
						//System.out.println("y = " + y + "x = " + x);
						flag = true;
						break;
					} else { //�ЂƂ����Ԃ��Ȃ�
						//System.out.println("�ЂƂ����Ԃ��Ȃ�"); //�f�o�b�N
					}
				}
			}
			if(flag){
				//System.out.println("�A�C�R���ς�����");
				guideCount++;
				buttonArray[y][x].setIcon(guideIcon);
			}

	}

	public void tellStory(int tCon){

		switch(tCon){
		case 1:
			strow1.setText("�ނ����A����܂łɒN���������Ƃ��Ȃ�"); //�����ݒ�ł͈�s��20�����ł�
			strow2.setText("�قǂ��ꂢ�ȁA���̎q�����܂����B");
			strow3.setText("");
			break;
		case 2:
			strow1.setText("���̎q�ɖ����Ȃ��΂����񂪐Ԃ��������");
			strow2.setText("��点�܂������A���ꂪ�悭���������̂�");
			strow3.setText("�ǂ��֍s���Ă��u�Ԃ����񂿂��v�ƌĂ΂�܂����B");
			break;
		case 3:
			strow1.setText("������A��e�͏��̎q�ɂ����܂����B");
			strow2.setText("�u���΂����񂪕a�C������������A�ǂ�ȋ��");
			strow3.setText("���Ă����ŁB���̃K���b�g�ƃo�^�[�̚�������Ăˁv");
			break;
		case 4:
			strow1.setText("�Ԃ����񂿂��͕ʂ̑��ɏZ�ނ��΂������");
			strow2.setText("���֌������āA�����ɏo�����܂����B");
			strow3.setText("");
			break;
		//�������番��
		case 5:
			if(myIcon == redIcon){
				//�Ԃ����񎋓_
				strow1.setText("���Ȃ����X�����������Ă���ƁA");
				strow2.setText("�������݂����������Ă��܂����B");
				strow3.setText("");
			}else {
				//�������ݎ��_
				strow1.setText("�����̂悤�Ɂu�����v��T���Ă����");
				strow2.setText("�Ԃ�����������Ԃ������̎q�������܂����B");
				strow3.setText("���Ȃ��͐��������邱�Ƃɂ��܂����B");
			}
			break;
		case 6:
			if(myIcon == redIcon){
				//�Ԃ����񎋓_
				strow1.setText("�u�ǂ��֍s���́H�v");
				strow2.setText("�u���΂�����̂������͂ǂ��H�v");
				strow3.setText("");
			}else {
				//�������ݎ��_
				strow1.setText("�u�ǂ��֍s���́H�v");
				strow2.setText("�u���΂�����̂������͂ǂ��H�v");
				strow3.setText("");
			}
			break;
		}
	}

	public void endTurn(){
		//System.out.println("endTurn���Ă΂ꂽ");
		setTurn(); //���Ȃ�or�����Ẵ^�[���ł��i���x���؂�ւ��j
		TurnCount++; //�^�[���J�E���g�𑝂₷
		comrow1.setText("����" + TurnCount + "�^�[���ڂł�"); //���x���̃Z�b�g
		guide(); //�K�C�h�̍쐬+�u����ꏊ�̔���i�Ȃ���ΏI���j
		tellStory(TurnCount);
	}

	//���s����ł�
	public void whichWin(){
		if(guideCount == 0){
			countSub = myIconCount - yourIconCount;
			if(countSub > 0){
				comrow1.setText("���Ȃ��̏����I");
				winner = 1;
			} else if(countSub==0){
				comrow1.setText("���������I");
				winner = 2;
			} else {
				comrow1.setText("���Ȃ��̕����I");
				winner = 0;
			}
			String msg = "JUDGE"+ " " + winner;
			//�T�[�o�ɏ��𑗂�
			out.println(msg);
			out.flush();
			repaint();
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
									//System.out.println("PlaySound time="+time);
									long endTime = System.currentTimeMillis()+time*1000/44100;
									clip.start();
									//System.out.println("PlaySound time="+(int)(time/44100));
									while(true){
											if(stopFlag){//stopFlag��true�ɂȂ����I��
													System.out.println("PlaySound stop by stopFlag");
													clip.stop();
													return;
											}
											//System.out.println("endTime="+endTime);
											//System.out.println("currentTimeMillis="+System.currentTimeMillis());
											if(endTime < System.currentTimeMillis()){//�Ȃ̒������߂�����I��
													//System.out.println("PlaySound stop by sound length");
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
					//System.out.println("StopSound");
			}

	}
	
	public void actionPerformed(ActionEvent e) {
        System.out.println("�A�N�V��������");
        System.out.println(e.getSource());
        String theCmd = e.getActionCommand();
        System.out.println("ActionCommand: "+theCmd);

        //theButton1���������Ƃ��ɁC�_�C�A���O��\������
        if(theCmd.equalsIgnoreCase("PUSH_Dialog")){
            WinDialogWindow dlg = new WinDialogWindow(this);
            setVisible(true);
        }
    }
	
	public void testDialog(){
		WinDialogWindow dlg = new WinDialogWindow(this);
        setVisible(true);
	}
	
	public static int returnWinner(){
		return winner;
	}

}

//�_�C�A���O�p�̃N���X
class WinDialogWindow extends JDialog implements ActionListener{
    WinDialogWindow(JFrame owner) {
        super(owner);//�Ăяo�����ƂƂ̐e�q�֌W�̐ݒ�D������R�����g�A�E�g����ƕʁX�̃_�C�A���O�ɂȂ�

		Container c = this.getContentPane();	//�t���[���̃y�C�����擾����
        c.setLayout(null);		//�������C�A�E�g�̐ݒ���s��Ȃ�
		int winner = MyClient.returnWinner();
		System.out.println("winner�̒l��" + winner);

        JButton theButton = new JButton();//�摜��\��t���郉�x��
		if(winner == 0){
			theButton.setText("���Ȃ��̂܂��I");
		} else if (winner == 1){
			theButton.setText("���Ȃ��̂����I");
		} else {
			theButton.setText("�Ђ��킯�I");
		}
        //ImageIcon theImage = new ImageIcon("win.jpg");//�Ȃɂ��摜�t�@�C�����_�E�����[�h���Ă���
        //theButton.setIcon(theImage);//���x����ݒ�
        theButton.setBounds(0,0,526,234);//�{�^���̑傫���ƈʒu��ݒ肷��D(x���W�Cy���W,x�̕�,y�̕��j
        theButton.addActionListener(this);//�{�^�����N���b�N�����Ƃ���actionPerformed�Ŏ󂯎�邽��
        c.add(theButton);//�_�C�A���O�ɓ\��t����i�\��t���Ȃ��ƕ\������Ȃ�

        setTitle("You Win!");//�^�C�g���̐ݒ�
        setSize(526, 234);//�傫���̐ݒ�
        setResizable(false);//�g��k���֎~//true�ɂ���Ɗg��k���ł���悤�ɂȂ�
        setUndecorated(true); //�^�C�g����\�����Ȃ�
        setModal(true);//������܂ŉ���G��Ȃ�����ifalse�ɂ���ƐG���j

        //�_�C�A���O�̑傫����\���ꏊ��ύX�ł���
        //�e�̃_�C�A���O�̒��S�ɕ\���������ꍇ�́C�e�̃E�B���h�E�̒��S���W�����߂āC�q�̃_�C�A���O�̑傫���̔������炷
        setLocation(owner.getBounds().x+owner.getWidth()/2-this.getWidth()/2,owner.getBounds().y+owner.getHeight()/2-this.getHeight()/2);
        setVisible(true);
    }
    public void actionPerformed(ActionEvent e) {
        this.dispose();//Dialog��p������
    }
}

