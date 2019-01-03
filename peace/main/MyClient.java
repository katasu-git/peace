import java.net.*;
import java.io.*;
import javax.swing.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.awt.Color;

public class MyClient extends JFrame implements MouseListener,MouseMotionListener {
	private JButton buttonArray[][];//�{�^���p�̔z��
	private Container c;
	private ImageIcon whiteIcon, redIcon, boardIcon,
			passIcon, resetIcon, guideIcon, redHoodIcon, wolfIcon, wmicon, rmicon;
	private int myColor;
	private int myTurn = 3; //myTurn==3�̂Ƃ������^�[���Ƃ���BmyTurn==0�͍��A1�͔��B
	private ImageIcon myIcon, yourIcon;
	private int flipNum = 0;
	private int TurnCount = 0;
	PrintWriter out;//�o�͗p�̃��C�^�[
	private JButton passButton;
	private JButton resetButton;
	private int myIconCount = 2, yourIconCount = 2, countSub =0;
	private String chara = "";
	int guideCount = 0;

	//�|�C���^�[�̃A�C�R��
	ImageIcon pointerIcon = new ImageIcon("icons/arrow.png");
	JLabel pointerLabel = new JLabel(pointerIcon);

	//�|�C���^�[�̃J�E���g
	JLabel pointcon = new JLabel();

	//�^�[���������A�C�R��
	ImageIcon myturnIcon = new ImageIcon("icons/yourturn.png");
	ImageIcon yourturnIcon = new ImageIcon("icons/rivalturn.png");
	JLabel imturnLabel = new JLabel(myturnIcon);

	//�^�[���J�E���g�̒��g
	JLabel tComment = new JLabel();

	//���O�̒��g
	JLabel logComment = new JLabel();
	JLabel logComment2 = new JLabel();
	JLabel logComment3 = new JLabel();

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
				imturnLabel.setIcon(yourturnIcon);

				if(myNumberInt % 2 == 0){
					myColor = 0;
					myIcon = whiteIcon;
					yourIcon = redIcon;
					setTurn();

					////////////////////////////////////���Ȃ��̓I�I�J�~///////////////////
					wolfIcon = new ImageIcon("icons/wolf.png");
					JLabel wolfLab = new JLabel(wolfIcon);
					c.add(wolfLab);
					wolfLab.setBounds(180,410,125,125);
					//wolfLab.setOpaque(true);

					//�������A�C�R��
					wmicon = new ImageIcon("icons/wolf-mini.png");
					JLabel wolfminiLab = new JLabel(wmicon);
					c.add(wolfminiLab);
					wolfminiLab.setBounds(365,10,50,50);
					//wolfLab.setOpaque(true);

					//�����Ă͂���������
					redHoodIcon = new ImageIcon("icons/redhood.png");
					JLabel redHoodLab = new JLabel(redHoodIcon);
					c.add(redHoodLab);
					redHoodLab.setBounds(30,410,125,125);
					//redHoodLab.setOpaque(true);

					//�������A�C�R��
					rmicon = new ImageIcon("icons/redhood-mini.png");
					JLabel redminilab = new JLabel(rmicon);
					c.add(redminilab);
					redminilab.setBounds(365,340,50,50);
					//wolfLab.setOpaque(true);

				} else {
					myColor = 1;
					myIcon = redIcon;
					yourIcon = whiteIcon;
					setTurn();

					//���Ȃ��͂���������
					redHoodIcon = new ImageIcon("icons/redhood.png");
					JLabel redHoodLab = new JLabel(redHoodIcon);
					c.add(redHoodLab);
					redHoodLab.setBounds(180,410,125,125);
					//redHoodLab.setOpaque(true);

					//�������A�C�R��
					rmicon = new ImageIcon("icons/redhood-mini.png");
					JLabel redminilab = new JLabel(rmicon);
					c.add(redminilab);
					redminilab.setBounds(365,10,50,50);
					//wolfLab.setOpaque(true);

					//���Ȃ��̓I�I�J�~
					wolfIcon = new ImageIcon("icons/wolf.png");
					JLabel wolfLab = new JLabel(wolfIcon);
					c.add(wolfLab);
					wolfLab.setBounds(30,410,125,125);
					//wolfLab.setOpaque(true);

					//�������A�C�R��
					wmicon = new ImageIcon("icons/wolf-mini.png");
					JLabel wolfminiLab = new JLabel(wmicon);
					c.add(wolfminiLab);
					wolfminiLab.setBounds(365,340,50,50);
					//wolfLab.setOpaque(true);

					//////////////////////////////////////////////////////////////////////

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
						} else if(cmd.equals("JUDGE")){
							int theColor = Integer.parseInt(inputTokens[1]);//myColor
							if(theColor == myColor){
								//System.out.println("���Ȃ��̕����ł�");
								tComment.setText("���Ȃ��̕����ł�");
							} else {
								//System.out.println("���Ȃ��̏����ł�");
								tComment.setText("���Ȃ��̏����ł�");
							}
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

		//if(theButton.getText().equals("PASS")){
		//	System.out.println("PURESS THE PASS BUTOON");
		//}

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

		} else if(theIcon.equals(passIcon)){
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

		} else if(theIcon.equals(resetIcon)){
				String msg = "RESET"+ " " + myColor;
				//�T�[�o�ɏ��𑗂�
				out.println(msg);
				out.flush();
				repaint();
		}
	}

	public void mouseEntered(MouseEvent e) {//�}�E�X���I�u�W�F�N�g�ɓ������Ƃ��̏���
		//System.out.println("�}�E�X��������");
	}

	public void mouseExited(MouseEvent e) {//�}�E�X���I�u�W�F�N�g����o���Ƃ��̏���
		//System.out.println("�}�E�X�E�o");
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
		resetIcon = new ImageIcon("icons/reset.png");
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
		pointcon.setBounds(398,178,30,30);
		pointcon.setText(Integer.toString(countSub));
		pointcon.setForeground(Color.decode("#c0bfbf"));
		//pointcon.setOpaque(true); //�w�i������
		pointcon.setFont(new Font("UD �f�W�^�� ���ȏ��� N-R", Font.PLAIN, 18));

		//�|�C���^�[ �ق�����g���̂ŊO�Ő錾
		c.add(pointerLabel);
		pointerLabel.setBounds(360-40+12,200-70+35,60,60);
		//pointerLabel.setOpaque(true);

		//���O�̒��g �ق��ł��g���̂Ő擪�Œ�`�B
		//JLabel logComment = new JLabel();
		c.add(logComment);
		logComment.setBounds(30,-20,300,300);
		logComment.setText("�����̃e�L�X�g�ł��B�����̃e�L�X�g�ł��B");
		logComment.setFont(new Font("UD �f�W�^�� ���ȏ��� N-R", Font.PLAIN, 16));
		logComment.setForeground(Color.decode("#FFFFFF"));

		//JLabel logComment2 = new JLabel();
		c.add(logComment2);
		logComment2.setBounds(30,20,300,300);
		logComment2.setText("�����̃e�L�X�g�ł��B�����̃e�L�X�g�ł��B");
		logComment2.setFont(new Font("UD �f�W�^�� ���ȏ��� N-R", Font.PLAIN, 16));
		logComment2.setForeground(Color.decode("#FFFFFF"));

		//JLabel logComment3 = new JLabel();
		c.add(logComment3);
		logComment3.setBounds(30,60,300,300);
		logComment3.setText("�����̃e�L�X�g�ł��B�����̃e�L�X�g�ł��B");
		logComment3.setFont(new Font("UD �f�W�^�� ���ȏ��� N-R", Font.PLAIN, 16));
		logComment3.setForeground(Color.decode("#FFFFFF"));

		//�p�X�{�^��
		passButton = new JButton(passIcon);
		c.add(passButton);
		passButton.setBounds(550,390,100,100);
		passButton.setOpaque(true);
		passButton.addMouseListener(this);
		passButton.setContentAreaFilled(false);
		//passButton.setBorderPainted(false);

		//reset�{�^��
		resetButton = new JButton(resetIcon);
		c.add(resetButton);
		resetButton.setBounds(680,390,100,100);
		resetButton.setOpaque(true);
		resetButton.addMouseListener(this);
		resetButton.setContentAreaFilled(false);
		//resetButton.setBorderPainted(false);

		/*//�^�[���J�E���g�̒��g
		//JLabel tComment = new JLabel();  �ق��ł��g���̂Ő擪�Œ�`�B
		c.add(tComment);
		tComment.setBounds(630,505,200,60);
		tComment.setText("����" + TurnCount + "�^�[���ڂł�");
		tComment.setFont(new Font("UD �f�W�^�� ���ȏ��� N-R", Font.PLAIN, 20));
		tComment.setForeground(Color.white);

		//�^�[���J�E���g�̉��n
		ImageIcon tConIcon = new ImageIcon("images/count.png");
		JLabel tConBack = new JLabel(tConIcon);
		c.add(tConBack);
		tConBack.setBounds(620,505,200,60);
		tConBack.setOpaque(true);*/

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
		//220
		//countSub = Math.abs(my - your);
		countSub = my - your;
		//������
		pointerLabel.setLocation(360-40+12,200-70+35);

		//���x���̈ʒu�͔��������Ă�������
		if(countSub == 0){
			pointcon.setText(" " + Integer.toString(countSub));
			pointcon.setLocation(398,178);
		} else if (countSub > 0){
			pointerLabel.setLocation(360-40+12,200-70+35 - countSub*7);
			pointcon.setText("+" + Integer.toString(countSub));
			pointcon.setLocation(398,178 - countSub*7);
		} else {
			pointerLabel.setLocation(360-40+12,200-70+35 - countSub*7);
			pointcon.setText(Integer.toString(countSub));
			pointcon.setLocation(398,178 - countSub*7);
		}
		repaint();
	}

	public void setTurn(){
		//����̏���
		if(myTurn == 3){
			if(myIcon.equals(whiteIcon)){
				imturnLabel.setIcon(myturnIcon);
				//���Ȃ��̃A�C�R���̂�������
				imturnLabel.setBounds(180,540,126,36);
				//����̃K�C�h�\��
				guide();
			} else {
				imturnLabel.setIcon(yourturnIcon);
				imturnLabel.setBounds(30,540,126,36);
			}
		} else {
			//���ڈȍ~�̏���
			Icon whichTurn = imturnLabel.getIcon();
			System.out.println(whichTurn);
			if(whichTurn.equals(myturnIcon)){
				imturnLabel.setIcon(yourturnIcon);
				imturnLabel.setBounds(30,540,126,36);
			} else {
				imturnLabel.setIcon(myturnIcon);
				imturnLabel.setBounds(180,540,126,36);
			}
		}
	}

	public void guide(){
		System.out.println("guide();���Ă΂ꂽ");
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
			logComment.setText("�ނ����A����܂łɒN���������Ƃ��Ȃ�"); //�����ݒ�ł͈�s��20�����ł�
			logComment2.setText("�قǂ��ꂢ�ȁA���̎q�����܂����B");
			logComment3.setText("");
			break;
		case 2:
			logComment.setText("���̎q�ɖ����Ȃ��΂����񂪐Ԃ��������");
			logComment2.setText("��点�܂������A���ꂪ�悭���������̂�");
			logComment3.setText("�ǂ��֍s���Ă��u�Ԃ����񂿂��v�ƌĂ΂�܂����B");
			break;
		case 3:
			logComment.setText("������A��e�͏��̎q�ɂ����܂����B");
			logComment2.setText("�u���΂����񂪕a�C������������A�ǂ�ȋ��");
			logComment3.setText("���Ă����ŁB���̃K���b�g�ƃo�^�[�̚�������Ăˁv");
			break;
		case 4:
			logComment.setText("�Ԃ����񂿂��͕ʂ̑��ɏZ�ނ��΂������");
			logComment2.setText("���֌������āA�����ɏo�����܂����B");
			logComment3.setText("");
			break;
		//�������番��
		case 5:
			if(myIcon == redIcon){
				//�Ԃ����񎋓_
				logComment.setText("���Ȃ����X�����������Ă���ƁA");
				logComment2.setText("�������݂����������Ă��܂����B");
				logComment3.setText("");
			}else {
				//�������ݎ��_
				logComment.setText("�����̂悤�Ɂu�����v��T���Ă����");
				logComment2.setText("�Ԃ�����������Ԃ������̎q�������܂����B");
				logComment3.setText("���Ȃ��͐��������邱�Ƃɂ��܂����B");
			}
			break;
		case 6:
			if(myIcon == redIcon){
				//�Ԃ����񎋓_
				logComment.setText("�u�ǂ��֍s���́H�v");
				logComment2.setText("�u���΂�����̂������͂ǂ��H�v");
				logComment3.setText("");
			}else {
				//�������ݎ��_
				logComment.setText("�u�ǂ��֍s���́H�v");
				logComment2.setText("�u���΂�����̂������͂ǂ��H�v");
				logComment3.setText("");
			}
			break;
		}
	}

	public void endTurn(){
		//System.out.println("endTurn���Ă΂ꂽ");
		setTurn(); //���Ȃ�or�����Ẵ^�[���ł��i���x���؂�ւ��j
		TurnCount++; //�^�[���J�E���g�𑝂₷
		tComment.setText("����" + TurnCount + "�^�[���ڂł�"); //���x���̃Z�b�g
		guide(); //�K�C�h�̍쐬+�u����ꏊ�̔���i�Ȃ���ΏI���j
		tellStory(TurnCount);
	}

	//���s����ł�
	public void whichWin(){
		if(guideCount == 0){
			countSub = myIconCount - yourIconCount;
			if(countSub > 0){
				tComment.setText("���Ȃ��̏����I");
			} else if(countSub==0){
				tComment.setText("���������I");
			} else {
				tComment.setText("���Ȃ��̕����I");
			}
		}
	}


}
