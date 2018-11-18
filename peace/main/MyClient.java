//�i���@�p�X����у^�[���J�E���g�̎��� �R�}�J�E���g����(11/8)
// ���Z�b�g�{�^���̎����@�������̊֐���(11/15)

import java.net.*;
import java.io.*;
import javax.swing.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
//�ǉ�

public class MyClient extends JFrame implements MouseListener,MouseMotionListener {
	private JButton buttonArray[][];//�{�^���p�̔z��
	private Container c;
	private ImageIcon blackIcon, whiteIcon, boardIcon, passIcon,resetIcon;
	private int myColor;
	private int myTurn = 3; //myTurn==3�̂Ƃ������^�[���Ƃ���BmyTurn==0�͍��A1�͔��B
	private ImageIcon myIcon, yourIcon;
	private int flipNum = 0;
	private int TurnCount = 0;
	PrintWriter out;//�o�͗p�̃��C�^�[
	JLabel turnLabel = new JLabel(" ");
	JLabel tCountLabel = new JLabel("Turn 0");
	JLabel myIconCLabel = new JLabel("�~" + "2");
	JLabel yourIconCLabel = new JLabel("�~" + "2");
	JLabel subLabel = new JLabel("����Ƃ̍���" + 0 + "�ł�");
	JTextArea area = new JTextArea("�����e�L�X�g");
	JLabel agentLabel = new JLabel(" ");
	private JButton passButton;
	private JButton resetButton;
	private int myIconCount = 2, yourIconCount = 2, countSub;

	public MyClient() {
		//���O�̓��̓_�C�A���O���J��
		String myName = JOptionPane.showInputDialog(null,"���O����͂��Ă�������","���O�̓���",JOptionPane.QUESTION_MESSAGE);
		if(myName.equals("")){
			myName = "No name";//���O���Ȃ��Ƃ��́C"No name"�Ƃ���
		}

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

				if(myNumberInt % 2 == 0){
					myColor = 0;
					myIcon = blackIcon;
					yourIcon = whiteIcon;
					System.out.println("myColor is " + myIcon); //�f�o�b�N
					turnLabel.setText("���Ȃ��̃^�[���ł�");

					ImageIcon iconColor = new ImageIcon("images/black.jpg");
					JLabel colorLabel = new JLabel(iconColor);
					c.add(colorLabel);
					colorLabel.setBounds(420,260,50,50);
					colorLabel.setForeground(Color.WHITE);

					ImageIcon yourIconColor = new ImageIcon("images/white.jpg");
					JLabel yourColorLabel = new JLabel(yourIconColor);
					c.add(yourColorLabel);
					yourColorLabel.setBounds(420,310,50,50);
					yourColorLabel.setForeground(Color.WHITE);

				} else {
					myColor = 1;
					myIcon = whiteIcon;
					yourIcon = blackIcon;
					System.out.println("myColor is " + myIcon); //�f�o�b�N
					turnLabel.setText("����̃^�[���ł�");

					ImageIcon iconColor = new ImageIcon("images/white.jpg");
	        JLabel colorLabel = new JLabel(iconColor);
	        c.add(colorLabel);
	        colorLabel.setBounds(420,260,50,50);
	        colorLabel.setForeground(Color.WHITE);

					ImageIcon yourIconColor = new ImageIcon("images/black.jpg");
					JLabel yourColorLabel = new JLabel(yourIconColor);
					c.add(yourColorLabel);
					yourColorLabel.setBounds(420,310,50,50);
					yourColorLabel.setForeground(Color.WHITE);

				}

				while(true) {
					String inputLine = br.readLine();//�f�[�^����s�������ǂݍ���ł݂�
					if (inputLine != null) {//�ǂݍ��񂾂Ƃ��Ƀf�[�^���ǂݍ��܂ꂽ���ǂ������`�F�b�N����
						System.out.println(inputLine);//�f�o�b�O�i����m�F�p�j�ɃR���\�[���ɏo�͂���
						String[] inputTokens = inputLine.split(" ");	//���̓f�[�^����͂��邽�߂ɁA�X�y�[�X�Ő؂蕪����
						String cmd = inputTokens[0];//�R�}���h�̎��o���D�P�ڂ̗v�f�����o��
						if(cmd.equals("MOVE")){//cmd�̕�����"MOVE"�����������ׂ�D��������true�ƂȂ�
							//MOVE�̎��̏���(�R�}�̈ړ��̏���)
							String theBName = inputTokens[1];//�{�^���̖��O�i�ԍ��j�̎擾
							int theBnum = Integer.parseInt(theBName);//�{�^���̖��O�𐔒l�ɕϊ�����
							int x = Integer.parseInt(inputTokens[2]);//���l�ɕϊ�����
							int y = Integer.parseInt(inputTokens[3]);//���l�ɕϊ�����

							int i = theBnum % 8;
							int j = theBnum / 8;

							buttonArray[i][j].setLocation(x,y);//�w��̃{�^�����ʒu��x,y�ɐݒ肷��
						}else if(cmd.equals("PLACE")){

							String theBName = inputTokens[1];
							int theBnum = Integer.parseInt(theBName);
							int theColor = Integer.parseInt(inputTokens[2]);
							int i = theBnum % 8;
							int j = theBnum / 8;

							  if(myTurn == 3){
								  if(theColor == 0){
										if(theColor == myColor){
											//���M���N���C�A���g�ł̏���
											buttonArray[i][j].setIcon(myIcon);
											agentLabel.setText("�u�����ŁB");
											myIconCount++; //�����̃J�E���g�𑝂₷
											myIconCLabel.setText("�~" + myIconCount);
											yourIconCLabel.setText("�~" + yourIconCount);
											turnLabel.setText("����̃^�[���ł�");
										  myTurn = 1; //myTurn==1�̂Ƃ���
									  } else {
											//���M��N���C�A���g�ł̏���
											buttonArray[i][j].setIcon(yourIcon);
											yourIconCount++; //����̃J�E���g�𑝂₷
											myIconCLabel.setText("�~" + myIconCount);
											yourIconCLabel.setText("�~" + yourIconCount);
											turnLabel.setText("���Ȃ��̃^�[���ł�");
											myTurn = 1; //myTurn==1�̂Ƃ���
										}
										countTurn();
									}
							  } else if(myTurn == 1){ //���̃^�[���̎�
								  if(theColor == 1){
									  if(theColor == myColor){
											//���M���N���C�A���g�ł̏���
											buttonArray[i][j].setIcon(myIcon);
											agentLabel.setText("�u�����ŁB");
											myIconCount++;
											myIconCLabel.setText("�~" + myIconCount);
											yourIconCLabel.setText("�~" + yourIconCount);
											turnLabel.setText("����̃^�[���ł�");
											myTurn = 0;
									  } else {
											//���M��N���C�A���g�ł̏���
											buttonArray[i][j].setIcon(yourIcon);
											yourIconCount++;
											myIconCLabel.setText("�~" + myIconCount);
											yourIconCLabel.setText("�~" + yourIconCount);
											turnLabel.setText("���Ȃ��̃^�[���ł�");
											myTurn = 0;
										}
										countTurn();
								  }
							  } else {
									if(theColor == 0){
										if(theColor == myColor){
											//���M���N���C�A���g�ł̏���
											buttonArray[i][j].setIcon(myIcon);
											agentLabel.setText("�u�����ŁB");
											myIconCount++;
											myIconCLabel.setText("�~" + myIconCount);
											yourIconCLabel.setText("�~" + yourIconCount);
											turnLabel.setText("����̃^�[���ł�");
											myTurn = 1;
									  } else {
											//���M��N���C�A���g�ł̏���
											buttonArray[i][j].setIcon(yourIcon);
											yourIconCount++;
											myIconCLabel.setText("�~" + myIconCount);
											yourIconCLabel.setText("�~" + yourIconCount);
											turnLabel.setText("���Ȃ��̃^�[���ł�");
											myTurn = 1;
										}
										countTurn();
									}
								}
								countSub = Math.abs(myIconCount - yourIconCount);
								subLabel.setText("����Ƃ̍���" + countSub + "�ł�");
						}else if(cmd.equals("FLIP")){

							String theBname = inputTokens[1];
							int theBnum = Integer.parseInt(theBname);
							int theColor = Integer.parseInt(inputTokens[2]);
							int i = theBnum % 8;
							int j = theBnum / 8;

							if(myTurn == 3){ //���̃^�[���̎�
									if(theColor == 0){ //���̖��߂����󂯕t���Ȃ�
										if(theColor == myColor){
											//���M���N���C�A���g�ł̏���
											myIconCount++;
											yourIconCount--;
											buttonArray[i][j].setIcon(myIcon);
									  } else {
											//���M��N���C�A���g�ł̏���
											yourIconCount++;
											myIconCount--;
											buttonArray[i][j].setIcon(yourIcon);
										}
									}
							} else if(myTurn == 0){
									if(theColor == 0){
										if(theColor == myColor){
											//���M���N���C�A���g�ł̏���
											myIconCount++;
											yourIconCount--;
											buttonArray[i][j].setIcon(myIcon);
										} else {
											//���M��N���C�A���g�ł̏���
											yourIconCount++;
											myIconCount--;
											buttonArray[i][j].setIcon(yourIcon);
										}
									}
							} else {
									if(theColor == 1){
										if(theColor == myColor){
											//���M���N���C�A���g�ł̏���
											myIconCount++;
											yourIconCount--;
											buttonArray[i][j].setIcon(myIcon);
										} else {
											//���M��N���C�A���g�ł̏���
											yourIconCount++;
											myIconCount--;
											buttonArray[i][j].setIcon(yourIcon);
										}
									}
							}
						}else if(cmd.equals("PASS")){
							int theTurn = Integer.parseInt(inputTokens[1]);
							int theColor = Integer.parseInt(inputTokens[2]);

							if(theColor == 0){
								if(theTurn == 3){ //���^�[���Ńp�X�̏ꍇ�͔��i1�j�ɐ؂�ւ���
									if(theColor == myColor){
										turnLabel.setText("����̃^�[���ł�");
									} else {
										turnLabel.setText("���Ȃ��̃^�[���ł�");
									}
									myTurn = 1;
									countTurn();
							  }
						  }
							if(theTurn == theColor){
								if(theTurn == 1){ //���̃^�[��
									if(theColor == myColor){
										turnLabel.setText("����̃^�[���ł�");
									} else {
										turnLabel.setText("���Ȃ��̃^�[���ł�");
									}
									myTurn = 0;
								} else {
									if(theColor == myColor){ //���̃^�[��
										turnLabel.setText("����̃^�[���ł�");
									} else {
										turnLabel.setText("���Ȃ��̃^�[���ł�");
									}
									myTurn = 1;
							  }
								countTurn();
							} else {
								System.out.println("����̃^�[���ɂ̓p�X�ł��܂���B");
							}
						} else if(cmd.equals("RESET")) {
							int theColor = Integer.parseInt(inputTokens[1]);
							if(theColor == 0){
								if(theColor == myColor){
									turnLabel.setText("���Ȃ��̃^�[���ł�");
								} else {
									turnLabel.setText("����̃^�[���ł�");
								}
							} else {
								if(theColor == myColor){
									turnLabel.setText("����̃^�[���ł�");
								} else {
									turnLabel.setText("���Ȃ��̃^�[���ł�");
								}
							}
							resetAll();
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

		if(theIcon.equals(boardIcon)){
			String theArrayIndex = theButton.getActionCommand();//�{�^���̔z��̔ԍ������o��
			int temp = Integer.parseInt(theArrayIndex);
			int tempx = temp / 8;
			int tempy = temp % 8;
			//System.out.println("tempx = " + tempx + "tempy = " + tempy);
			if(judgeButton(tempy, tempx)){
				//������
				String msg = "PLACE" + " " + theArrayIndex + " " + myColor + " " + theIcon;
				//�T�[�o�ɏ��𑗂�
				out.println(msg);//���M�f�[�^���o�b�t�@�ɏ����o��
				out.flush();//���M�f�[�^���t���b�V���i�l�b�g���[�N��ɂ͂��o���j���� 70�s��
				repaint();//��ʂ̃I�u�W�F�N�g��`�悵����
			} else {
				//�u���Ȃ�
				//System.out.println("�����ɂ͔z�u�ł��܂���");
				agentLabel.setText("�����ɂ͒u����ւ�ŁB");
			}
			repaint();//��ʂ̃I�u�W�F�N�g��`�悵����

		} else if(theIcon.equals(passIcon)){
				String msg = "PASS" + " " + myTurn + " " + myColor;
				//�T�[�o�ɏ��𑗂�
				out.println(msg);
				out.flush();
				repaint();

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
							//System.out.println("�Ђ�����Ԃ��ԍ��͂���ł�" + theArrayIndex);

							if(!(IconComp.equals(boardIcon))){ //����̐F�̎��̂ݑ��M���܂�(11/8�C��)
								if(myColor == 0){
									if(!(IconComp.equals(blackIcon))){
										String msg = "FLIP"+" "+theArrayIndex+" "+myColor;
										out.println(msg);
										out.flush();
										repaint();
									}
								} else {
									if(!(IconComp.equals(whiteIcon))){
										String msg = "FLIP"+" "+theArrayIndex+" "+myColor;
										out.println(msg);
										out.flush();
										repaint();
									}
								}
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
				}
			}catch(ArrayIndexOutOfBoundsException e){
				//System.out.println("���̕����ɂ͔Ֆʂ�����܂���");
				flipNum = 0;
				break;
			}
		}
		return flipNum;
	}

	public void setUp(){
		//�E�B���h�E���쐬����
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//�E�B���h�E�����Ƃ��ɁC����������悤�ɐݒ肷��
		setTitle("MyClient");//�E�B���h�E�̃^�C�g����ݒ肷��
		setSize(600,700);//�E�B���h�E�̃T�C�Y��ݒ肷��
		c = getContentPane();//�t���[���̃y�C�����擾����

		//�A�C�R���̐ݒ�
		whiteIcon = new ImageIcon("images/White.jpg");
		blackIcon = new ImageIcon("images/Black.jpg");
		boardIcon = new ImageIcon("images/GreenFrame.jpg");
		passIcon = new ImageIcon("images/pass.jpg");
		resetIcon = new ImageIcon("images/reset.jpg");

		c.setLayout(null);//�������C�A�E�g�̐ݒ���s��Ȃ�
		//�{�^���̐���

		buttonArray = new JButton[8][8];

		for(int j=0;j<8;j++){
			for(int i=0;i<8;i++){
			buttonArray[i][j] = new JButton(boardIcon);//�{�^���ɃA�C�R����ݒ肷��
			c.add(buttonArray[i][j]);//�y�C���ɓ\��t����
			buttonArray[i][j].setBounds(i*50+10,j*50+10,50,50);//�{�^���̑傫���ƈʒu��ݒ肷��D(x���W�Cy���W,x�̕�,y�̕��j
			buttonArray[i][j].addMouseListener(this);//�{�^�����}�E�X�ł�������Ƃ��ɔ�������悤�ɂ���
			buttonArray[i][j].addMouseMotionListener(this);//�{�^�����}�E�X�œ��������Ƃ����Ƃ��ɔ�������悤�ɂ���
			buttonArray[i][j].setActionCommand(Integer.toString(j*8+i));//�{�^���ɔz��̏���t������i�l�b�g���[�N����ăI�u�W�F�N�g�����ʂ��邽�߁j
		  }
		}

		buttonArray[3][3].setIcon(whiteIcon);
		buttonArray[4][3].setIcon(blackIcon);
		buttonArray[3][4].setIcon(blackIcon);
		buttonArray[4][4].setIcon(whiteIcon);

		//pass�{�^��
		passButton = new JButton(passIcon);
		c.add(passButton);//�y�C���ɓ\��t����
		passButton.setBounds(430, 10 ,50, 50);
		passButton.addMouseListener(this);

		//reset�{�^��
		resetButton = new JButton(resetIcon);
		c.add(resetButton);//�y�C���ɓ\��t����
		resetButton.setBounds(490, 10 ,50, 50);
		resetButton.addMouseListener(this);

		//�^�[�����x���̏����ݒ�
		c.add(turnLabel);
		turnLabel.setBounds(430,80,150,50);
		turnLabel.addMouseListener(this);//�{�^�����}�E�X�ł�������Ƃ��ɔ�������悤�ɂ���
		turnLabel.setForeground(Color.BLACK); //�����F�̐ݒ�DColor�̐ݒ�́C���̃y�[�W�����ĉ������@http://www.javadrive.jp/tutorial/color/
		turnLabel.setBackground(Color.WHITE); //�����̔w�i�F�̐ݒ�D
		turnLabel.setOpaque(true);//���x����s�����ɂ��Ȃ��Ɣw�i�F�������Ȃ��̂ŁC�s�����ɂ��邷��

		//�^�[���J�E���g���x��
		c.add(tCountLabel);
		tCountLabel.setBounds(430,140,100,50);
		tCountLabel.addMouseListener(this);
		tCountLabel.setForeground(Color.BLACK);
		tCountLabel.setBackground(Color.WHITE);
		tCountLabel.setOpaque(true);

		c.add(subLabel);
		subLabel.setBounds(430,200,120,50);
		subLabel.addMouseListener(this);
		subLabel.setForeground(Color.BLACK);
		subLabel.setBackground(Color.WHITE);
		subLabel.setOpaque(true);

		//�A�C�R���J�E���g���x��
		c.add(myIconCLabel);
		myIconCLabel.setBounds(480,260,50,50);
		myIconCLabel.addMouseListener(this);
		myIconCLabel.setForeground(Color.BLACK);
		myIconCLabel.setBackground(Color.WHITE);
		myIconCLabel.setOpaque(true);

		c.add(yourIconCLabel);
		yourIconCLabel.setBounds(480,310,50,50);
		yourIconCLabel.addMouseListener(this);
		yourIconCLabel.setForeground(Color.BLACK);
		yourIconCLabel.setBackground(Color.WHITE);
		yourIconCLabel.setOpaque(true);

		//�G�[�W�F���g�̏����ݒ�

		c.add(agentLabel);
		agentLabel.setBounds(250,450,200,100);
		agentLabel.addMouseListener(this);
		agentLabel.setForeground(Color.BLACK);
		//agentLabel.setBackground(Color.WHITE);
		agentLabel.setOpaque(true);
		agentLabel.setText("NULLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL");
		agentLabel.setFont(new Font("Serif", Font.PLAIN, 20));

		ImageIcon fukiIcon = new ImageIcon("images/fuki.png");
		JLabel fukiLabel = new JLabel(fukiIcon);
		c.add(fukiLabel);
		fukiLabel.setBounds(150,390,400,250);
		fukiLabel.setOpaque(true);

		ImageIcon agentIcon = new ImageIcon("images/black_man.png");
		JLabel agentLabelimg = new JLabel(agentIcon);
		c.add(agentLabelimg);
		agentLabelimg.setBounds(0,450,200,200);
		agentLabelimg.setOpaque(true);
	}

	//���Z�b�g�̏���
	public void resetAll(){
		for(int j=0;j<8;j++){
			for(int i=0;i<8;i++){
			buttonArray[i][j].setIcon(boardIcon);
			}
		}

		buttonArray[3][3].setIcon(whiteIcon);
		buttonArray[4][3].setIcon(blackIcon);
		buttonArray[3][4].setIcon(blackIcon);
		buttonArray[4][4].setIcon(whiteIcon);

		myTurn = 3;
		TurnCount = 0;
		tCountLabel.setText("Turn " + TurnCount);
		myIconCount = 2;
		yourIconCount = 2;
		myIconCLabel.setText("�~" + myIconCount);
		yourIconCLabel.setText("�~" + yourIconCount);
		countSub = Math.abs(myIconCount - yourIconCount);
		subLabel.setText("����Ƃ̍���" + countSub + "�ł�");
	}

	//�^�[���J�E���g�𑝂₵�āA�I������ɂ��Ȃ�
	public void countTurn(){
		TurnCount++;
		tCountLabel.setText("Turn " + TurnCount);
		//System.out.println("TurnCount = " + TurnCount); //�f�o�b�N
	}

	public void agent(){

	}




}
