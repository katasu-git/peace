import java.net.*;
import java.io.*;
import javax.swing.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.awt.Color;

//枠線
import javax.swing.border.LineBorder;
import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;

import java.io.File;//音楽再生時に必要
import javax.sound.sampled.AudioFormat;//音楽再生時に必要
import javax.sound.sampled.AudioSystem;//音楽再生時に必要
import javax.sound.sampled.Clip;//音楽再生時に必要
import javax.sound.sampled.DataLine;//音楽再生時に必要

//タイマー
import java.text.ParseException;
import java.util.Timer;
import java.util.TimerTask;

public class littleRed extends JFrame implements MouseListener,MouseMotionListener,ActionListener {
	private JButton buttonArray[][];//ボタン用の配列
	private Container c;
	private ImageIcon whiteIcon, redIcon, boardIcon,pactiveIcon,ractiveIcon,
			passIcon, circleIcon, guideIcon, redHoodIcon, wolfIcon, wmicon, rmicon;
	private int myColor;
	private int myTurn = 3; //myTurn==3のとき初期ターンとする。myTurn==0は黒、1は白。
	private int myIconCount = 2, yourIconCount = 2, countSub =0;
	private int passCount = 0;
	private int guideCount = 0;
	private ImageIcon myIcon, yourIcon;
	private int flipNum = 0;
	private JButton passButton, resetButton, pactiveButton, ractiveButton;
	private String chara = "";
	static int TurnCount = 0;
	static String winnerStr = "wolf"; // or "red" or "draw"
	static int difCounter = 3; // 0 = 差が5を超えていない 1 = 差が一度でも5以上になった 2 = 差が10以上になった //3=>OP処理
	static int op = 0;
	SoundPlayer theSoundPlayer1;//どこからでもアクセスできるように，クラスのメンバとして宣言
	PrintWriter out;//出力用のライター

	//ポインターのアイコン
	ImageIcon pointerIcon = new ImageIcon("icons/arrow.png");
	JLabel pointerLabel = new JLabel(pointerIcon);

	//ポインターのカウント
	JLabel pointcon = new JLabel();

	//ターンを示すアイコン
	ImageIcon myturnIcon = new ImageIcon("icons/yourturn.png");
	ImageIcon rivalTurnIcon = new ImageIcon("icons/rivalturn.png");
	JLabel turnLabel = new JLabel(myturnIcon);

	//暗転用の画像
	static ImageIcon back90Icon = new ImageIcon("icons/background90.png");
	static JLabel back90 = new JLabel(back90Icon);

	//ログ
	JLabel strow1 = new JLabel();
	JLabel strow2 = new JLabel();
	JLabel strow3 = new JLabel();
	JLabel strow4 = new JLabel();

	JLabel comrow1 = new JLabel();
	JLabel comrow2 = new JLabel();

	JLabel leftTurn = new JLabel();

	public littleRed() {

		/*
		//名前の入力ダイアログを開く
		String myName = JOptionPane.showInputDialog(null,"名前を入力してください","名前の入力",JOptionPane.QUESTION_MESSAGE);
		if(myName.equals("")){
			myName = "No name";//名前がないときは，"No name"とする
		}
		*/

		String myName = "No name";

		//IPアドレスの入力
		String myIp = JOptionPane.showInputDialog(null,"IPアドレスを入力。オフラインの場合は何も入力しなくてOK","IPアドレスの入力",JOptionPane.QUESTION_MESSAGE);
		if(myIp.equals("")){
			myIp = "localhost";//ないときは，localhostとする
		}

		setUp();

		//サーバに接続する
		Socket socket = null;
		try {
			//"localhost"は，自分内部への接続．localhostを接続先のIP Address（"133.42.155.201"形式）に設定すると他のPCのサーバと通信できる
			//10000はポート番号．IP Addressで接続するPCを決めて，ポート番号でそのPC上動作するプログラムを特定する
			socket = new Socket(myIp, 10000);
		} catch (UnknownHostException e) {
			System.err.println("ホストの IP アドレスが判定できません: " + e);
		} catch (IOException e) {
			System.err.println("エラーが発生しました: " + e);
		}

		MesgRecvThread mrt = new MesgRecvThread(socket, myName);//受信用のスレッドを作成する
		mrt.start();//スレッドを動かす（Runが動く）
	}

	//メッセージ受信のためのスレッド
	public class MesgRecvThread extends Thread {

		Socket socket;
		String myName;

		public MesgRecvThread(Socket s, String n){
			socket = s;
			myName = n;
		}

		//通信状況を監視し，受信データによって動作する
		public void run() {
			try{
				InputStreamReader sisr = new InputStreamReader(socket.getInputStream());
				BufferedReader br = new BufferedReader(sisr);
				out = new PrintWriter(socket.getOutputStream(), true);
				out.println(myName);//接続の最初に名前を送る

				String myNumberStr = br.readLine();
				int myNumberInt = Integer.parseInt(myNumberStr);

				c.add(turnLabel);
				turnLabel.setBounds(180,540,126,36);
				turnLabel.setIcon(rivalTurnIcon);

				//オオカミ
				wolfIcon = new ImageIcon("icons/wolf.png");
				JLabel wolfLab = new JLabel(wolfIcon);
				c.add(wolfLab);

				//小さいアイコン
				wmicon = new ImageIcon("icons/wolf-mini.png");
				JLabel wolfminiLab = new JLabel(wmicon);
				c.add(wolfminiLab);

				//あかずきん
				redHoodIcon = new ImageIcon("icons/redhood.png");
				JLabel redHoodLab = new JLabel(redHoodIcon);
				c.add(redHoodLab);

				//小さいアイコン
				rmicon = new ImageIcon("icons/redhood-mini.png");
				JLabel redminilab = new JLabel(rmicon);
				c.add(redminilab);

				if(myNumberInt % 2 == 0){
					myColor = 0;
					myIcon = whiteIcon;
					yourIcon = redIcon;
					setTurn();

					//あなたはオオカミ
					wolfLab.setBounds(180,410,125,125);
					wolfminiLab.setBounds(365,10,50,50);

					//あいてはあかずきん
					redHoodLab.setBounds(30,410,125,125);
					redminilab.setBounds(365,340,50,50);
					comrow1.setText("キミはオオカミ");

				} else {
					myColor = 1;
					myIcon = redIcon;
					yourIcon = whiteIcon;
					setTurn();

					//あなたはあかずきん
					redHoodLab.setBounds(180,410,125,125);
					redminilab.setBounds(365,10,50,50);

					//あいてはオオカミ
					wolfLab.setBounds(30,410,125,125);
					wolfminiLab.setBounds(365,340,50,50);
					comrow1.setText("アナタはあかずきん");

				}
				//背景画像は最後に定義する
				ImageIcon mainImIcon = new ImageIcon("icons/main-frame.jpg");
				JLabel mainImLabel = new JLabel(mainImIcon);
				c.add(mainImLabel);
				mainImLabel.setBounds(0,0,800,600);

				while(true) {
					String inputLine = br.readLine();//データを一行分だけ読み込んでみる
					if (inputLine != null) {//読み込んだときにデータが読み込まれたかどうかをチェックする
						//System.out.println(inputLine);//デバッグ（動作確認用）にコンソールに出力する
						String[] inputTokens = inputLine.split(" ");	//入力データを解析するために、スペースで切り分ける
						String cmd = inputTokens[0];//コマンドの取り出し．１つ目の要素を取り出す
						if(cmd.equals("MOVE")){
							//必要のない処理
						} else if(cmd.equals("PLACE")) {

							String theBName = inputTokens[1];
							int theBnum = Integer.parseInt(theBName);
							int theColor = Integer.parseInt(inputTokens[2]);
							int i = theBnum % 8;
							int j = theBnum / 8;

							if(myTurn == 3){
								if(theColor == myColor){
									//送信元
									buttonArray[i][j].setIcon(myIcon);
									myIconCount++; //自分のカウントを増やす
								} else {
									//送信先
									buttonArray[i][j].setIcon(yourIcon);
									yourIconCount++; //相手のカウントを増やす
								}
								myTurn = 1;
							} else if(myTurn == 0){
								if(theColor == myColor){
									//送信元
									buttonArray[i][j].setIcon(myIcon);
									myIconCount++; //自分のカウントを増やす
								} else {
									//送信先
									buttonArray[i][j].setIcon(yourIcon);
									yourIconCount++; //相手のカウントを増やす
								}
								myTurn = 1;
							} else {
								if(theColor == myColor){
									//送信元
									buttonArray[i][j].setIcon(myIcon);
									myIconCount++;
								} else {
									//送信先クライアントでの処理
									buttonArray[i][j].setIcon(yourIcon);
									yourIconCount++;
								}
								myTurn = 0;
							}
							movePointer(myIconCount, yourIconCount); //相手との差を算出
							endTurn();

						} else if(cmd.equals("FLIP")) {

							String theBname = inputTokens[1];
							int theBnum = Integer.parseInt(theBname);
							int theColor = Integer.parseInt(inputTokens[2]);
							int i = theBnum % 8;
							int j = theBnum / 8;

							if(myTurn == 3){
								if(theColor == myColor){
									//送信元
									myIconCount++;
									yourIconCount--;
									buttonArray[i][j].setIcon(myIcon);
								} else {
									//送信先
									yourIconCount++;
									myIconCount--;
									buttonArray[i][j].setIcon(yourIcon);
								}
							} else if(myTurn == 0){
								if(theColor == myColor){
									//送信元
									myIconCount++;
									yourIconCount--;
									buttonArray[i][j].setIcon(myIcon);
								} else {
									//送信先
									yourIconCount++;
									myIconCount--;
									buttonArray[i][j].setIcon(yourIcon);
								}
							} else {
								if(theColor == myColor){
									//送信元
									myIconCount++;
									yourIconCount--;
									buttonArray[i][j].setIcon(myIcon);
								} else {
									//送信先
									yourIconCount++;
									myIconCount--;
									buttonArray[i][j].setIcon(yourIcon);
								}
							}
						}else if(cmd.equals("PASS")){
							int theTurn = Integer.parseInt(inputTokens[1]);//myTurn
							int theColor = Integer.parseInt(inputTokens[2]);//myColor

							//初ターンでパス
							if(myTurn == 3){
								myTurn = 1;
							} else if(myTurn == 0){
								myTurn = 1;
							} else {
								myTurn = 0;
							}
							endTurn();

						} else if(cmd.equals("DIF")) {
							//DIFの処理
							int countSub = Integer.parseInt(inputTokens[1]);
							if(TurnCount < 12 && countSub >= 9){
								difCounter = 2;
							} else if(difCounter != 2 && countSub >= 4) {
								difCounter = 1;
							}

						} else if(cmd.equals("JUDGE")){
							testDialog();
							System.exit(0); //強制終了の処理

						} else if(cmd.equals("GUIDE")){
							int theGuide = Integer.parseInt(inputTokens[1]);//guideCount
							guideCount = theGuide; //ガイドの数を両方に適応、共有する

							//置けなければ勝敗判定
							if(guideCount == 0) {
								whichWin();
								if(getWhichTurn()){
									String msg = "JUDGE";
									//サーバに情報を送る
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
				System.err.println("エラーが発生しました: " + e);
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
			String theArrayIndex = theButton.getActionCommand();//ボタンの配列の番号を取り出す
			int temp = Integer.parseInt(theArrayIndex);
			int tempx = temp / 8;
			int tempy = temp % 8;
			if(judgeButton(tempy, tempx)){
				//おける
				if(getWhichTurn()){
					String msg = "PLACE" + " " + theArrayIndex + " " + myColor + " " + theIcon;
					//サーバに情報を送る
					out.println(msg);
					out.flush();
					repaint();
				} else {
					playSound("sounds/cantPlace.wav");
				}

			} else {
				//置けない
				playSound("sounds/cantPlace.wav");
			}
			repaint();

		} else if(theIcon.equals(pactiveIcon)){ //active状態のアイコンに指定

				if(getWhichTurn()){
					if(passCount == 0){
						passCount++; //パスカウントを増やす
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

	//マウスが入ったときの処理
	public void mouseEntered(MouseEvent e) {

		JButton theButton = (JButton)e.getComponent();
		Icon theIcon = theButton.getIcon();

		if(theIcon.equals(boardIcon) || theIcon.equals(guideIcon) || theIcon.equals(redIcon) ||theIcon.equals(whiteIcon)){
		//カタカタ鳴らす
		playSound("sounds/kot.wav");

		} else	if (theIcon.equals(passIcon)){
			//重ねておいて表示・非表示を切り替える
			passButton.setVisible(false);
			pactiveButton.setVisible(true);

		}
	}

	//マウスが出たときの処理
	public void mouseExited(MouseEvent e) {
		JButton theButton = (JButton)e.getComponent();
		Icon theIcon = theButton.getIcon();
		if(theIcon.equals(boardIcon) || theIcon.equals(guideIcon) || theIcon.equals(redIcon) ||theIcon.equals(whiteIcon)){

		} else	if (theIcon.equals(pactiveIcon)){
			//色反転
			passButton.setVisible(true);
			pactiveButton.setVisible(false);

		} else if (theIcon.equals(ractiveIcon)){
			//色反転
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
						//一つ以上裏返せる場合
						IconComp = buttonArray[y+j][x+i].getIcon();
						flag = true;

						for(int dy=j, dx=i, k=0; k<flipNum; k++, dy+=j, dx+=i){
							//ボタンの位置情報を作る
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
						//ひとつも裏返せない
					}
				}
			}
			return flag;
	}

	public int flipButtons(int y, int x, int j, int i){
		Icon IconRev;
		flipNum = 0; //初期化
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

	//UIの生成
	public void setUp(){
		//ウィンドウを作成
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("little Red");//ウィンドウのタイトル
		setSize(812,635);//ウィンドウのサイズ
		c = getContentPane();

		//fix
		testDialog();
		difCounter = 0;
		if(op == 1){
			System.exit(0);
		}

		//アイコンの設定
		redIcon = new ImageIcon("icons/r-icon.png");
		whiteIcon = new ImageIcon("icons/w-icon.png");
		boardIcon = new ImageIcon("icons/b-icon.png");
		passIcon = new ImageIcon("icons/pass.png");
		pactiveIcon = new ImageIcon("icons/pactive.png");
		circleIcon = new ImageIcon("icons/circle.png");
		guideIcon = new ImageIcon("icons/g-icon.png");

		//暗転用の画像
		c.add(back90);
		back90.setBounds(0,0,800,600);
		back90.setVisible(false);

		c.setLayout(null);//自動レイアウトの設定を行わない
		//ボタンの生成
		buttonArray = new JButton[8][8];

		for(int j=0;j<8;j++){
			for(int i=0;i<8;i++){
			buttonArray[i][j] = new JButton(boardIcon);//ボタンにアイコンを設定する
			c.add(buttonArray[i][j]);//ペインに貼り付ける

			buttonArray[i][j].setBounds(i*45+428,j*45+15,45,45);//ボタンの大きさと位置を設定する．(x座標，y座標,xの幅,yの幅）
			buttonArray[i][j].addMouseListener(this);//ボタンをマウスでさわったときに反応するようにする
			buttonArray[i][j].addMouseMotionListener(this);//ボタンをマウスで動かそうとしたときに反応するようにする
			buttonArray[i][j].setActionCommand(Integer.toString(j*8+i));//ボタンに配列の情報を付加する（ネットワークを介してオブジェクトを識別するため）
			buttonArray[i][j].setContentAreaFilled(false); //ボタン背景の透明化
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
		//pointcon.setOpaque(true); //背景透明化
		pointcon.setFont(new Font("UD デジタル 教科書体 N-B", Font.BOLD, 15));

		c.add(pointerLabel);
		pointerLabel.setBounds(360-40+13,200-70+35,60,60);

		//ログの中身 ほかでも使うのでグローバルで定義。
		//JLabel strow1 = new JLabel();
		c.add(strow1);
		strow1.setBounds(20,-10,300,300);
		strow1.setText("ここは共通の");
		strow1.setFont(new Font("UD デジタル 教科書体 N-B", Font.BOLD, 16));
		strow1.setForeground(new Color(192,191,191,255));

		c.add(strow2);
		strow2.setBounds(20,50,300,300);
		strow2.setText("ストーリーボードです");
		strow2.setFont(new Font("UD デジタル 教科書体 N-B", Font.BOLD, 16));
		strow2.setForeground(new Color(192,191,191,255));

		c.add(strow3);
		strow3.setBounds(20,110,300,300);
		strow3.setText("物語の進行とともに");
		strow3.setFont(new Font("UD デジタル 教科書体 N-B", Font.BOLD, 16));
		strow3.setForeground(new Color(192,191,191,255));

		//ここはMAX15文字
		c.add(strow4);
		strow4.setBounds(20,170,300,300);
		strow4.setText("テキストが変化します");
		strow4.setFont(new Font("UD デジタル 教科書体 N-B", Font.BOLD, 16));
		strow4.setForeground(new Color(192,191,191,255));

		c.add(comrow1);
		comrow1.setBounds(365,412,200,100);
		comrow1.setFont(new Font("UD デジタル 教科書体 N-B", Font.BOLD, 16));
		comrow1.setForeground(new Color(192,191,191,255));

		c.add(comrow2);
		comrow2.setBounds(384,452,200,100);
		comrow2.setText("唐突なメタ発言");
		comrow2.setFont(new Font("UD デジタル 教科書体 N-B", Font.BOLD, 16));
		comrow2.setForeground(new Color(192,191,191,255));

		//パスボタン
		passButton = new JButton(passIcon);
		c.add(passButton);
		passButton.setBounds(550,390,100,100);
		passButton.setOpaque(true);
		passButton.addMouseListener(this);
		passButton.setContentAreaFilled(false);
		passButton.setVisible(true);
		passButton.setBorder(new LineBorder(new Color(102,102,102,255), 1, true)); //枠の色設定

		//アクティブ状態
		pactiveButton = new JButton(pactiveIcon);
		c.add(pactiveButton);
		pactiveButton.setBounds(550,390,100,100);
		pactiveButton.setOpaque(true);
		pactiveButton.addMouseListener(this);
		pactiveButton.setContentAreaFilled(false);
		pactiveButton.setVisible(false);
		pactiveButton.setBorder(new LineBorder(new Color(102,102,102,255), 1, true)); //枠の色設定
		//pactiveButton.setBorderPainted(false);

		c.add(leftTurn);
		leftTurn.setBounds(705,390,100,100);
		leftTurn.setFont(new Font("UD デジタル 教科書体 N-B", Font.BOLD, 50));
		leftTurn.setForeground(new Color(192,191,191,255));
		leftTurn.setText("20");

		//残りターン数
		resetButton = new JButton(circleIcon);
		c.add(resetButton);
		resetButton.setBounds(680,390,100,100);
		//resetButton.setOpaque(true);
		resetButton.addMouseListener(this);
		resetButton.setContentAreaFilled(false);
		resetButton.setVisible(true);
		resetButton.setBorder(new LineBorder(new Color(102,102,102,255), 1, true)); //枠の色設定
	}

	public void movePointer(int my, int your){

		countSub = my - your; //コマの数の差を出す
		setDifCounter(countSub); //差によってエンディングが変わる

		//移送距離は 基準値 + 差分*9
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
				//5回実行で停止
				if ( cnt >= 9 ) timer.cancel();
			}
		};
		//初期ディレイとインターバル
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

	//どちらのターンかを示すラベルの切り替え
	public void setTurn(){
		if(myTurn == 3){
			//////初回の処理 いじらなくてok////////////////////////////////////////////
			if(myIcon.equals(whiteIcon)){
				turnLabel.setIcon(myturnIcon);
				//あなたのアイコンのすぐ下に
				turnLabel.setBounds(180,540,126,36);
				//初回のガイド表示
				guide();
			} else {
				turnLabel.setIcon(rivalTurnIcon);
				turnLabel.setBounds(30,540,126,36);
			}
		///////////////////////////////////////////////////////////////////////////
		} else {
			//二回目以降の処理
			Icon whichTurn = turnLabel.getIcon();
			if(whichTurn.equals(myturnIcon)){

				turnLabel.setLocation(180, 540 );//初期化
				Timer timer = new Timer(false);
				TimerTask task = new TimerTask() {

					int cnt=0;

					@Override
					public void run() {
						turnLabel.setLocation(turnLabel.getX() - 10, 540 );
						cnt++;
						//15回実行で停止
						if ( cnt >= 15 ) timer.cancel();
					}
				};
				//初期ディレイとインターバル
				timer.schedule(task, 0, 10);
				turnLabel.setIcon(rivalTurnIcon);

			} else {

				turnLabel.setLocation(30, 540 );//初期化
				Timer timer = new Timer(false);
				TimerTask task = new TimerTask() {

					int cnt=0;

					@Override
					public void run() {
						turnLabel.setLocation(turnLabel.getX() + 10, 540 );
						cnt++;
						//5回実行で停止
						if ( cnt >= 15 ) timer.cancel();
					}
				};
				//初期ディレイとインターバル
				timer.schedule(task, 0, 10);
				turnLabel.setIcon(myturnIcon);
			}
		}
	}

	public void guide(){
		//初期化
		guideCount = 0;
		Icon IconComp;
		Icon whichTurn = turnLabel.getIcon();
		if(whichTurn.equals(myturnIcon)){
			for(int i=0; i<8; i++){
				for(int j=0; j<8; j++){
					IconComp = buttonArray[i][j].getIcon();
					if(IconComp == boardIcon){
						//緑のとき探索開始
						judgeButton2(i,j);
					}
				}
			}
			//forループ終わった後でガイドの数を送信、勝敗判定
			String msg = "GUIDE" + " " + guideCount;
			out.println(msg);
			out.flush();
			repaint();
		} else {
			//相手の場合はガイドをリセット
			for(int i=0; i<8; i++){
				for(int j=0; j<8; j++){
					//すでにあるガイドは消す
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
						//一つ以上裏返せる場合
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

	//この手前でmovePointerが動いてるよ！
	//ターンの終了処理いろいろ
	public void endTurn(){
		setPass(); //パス
		setTurn(); //ターンのラベル切り替え
		TurnCount++; //ターンカウントを増やす
		leftTurn.setText(Integer.toString(20 - TurnCount));
		if(20 - TurnCount < 10){
			leftTurn.setBounds(717,390,100,100);
		}
		if(20 - TurnCount < 5){
			leftTurn.setForeground(Color.decode("#902D3E"));
		}
		guide(); //ガイドの作成+置ける場所の判定（なければ終了）

		//ディレイ
		try {
			Thread.sleep(250);
		} catch(InterruptedException e){
				e.printStackTrace();
		}
		tellStory(TurnCount); //ストーリーを進める
	}

	//勝敗判定です
	public void whichWin(){

			countSub = myIconCount - yourIconCount;
			if(countSub > 0){
				if(getWolfOrRed()){
				//オオカミ
				winnerStr = "wolf";
				} else {
				//あかずきん
				winnerStr = "red";
				}
			} else if(countSub==0){
				winnerStr = "draw";
			} else {
				if(getWolfOrRed()){
				//オオカミ
				winnerStr = "red";
				} else {
				//あかずきん
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
							//clip.setLoopPoints(0,clip.getFrameLength());//無限ループとなる
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
									long time = (long)clip.getFrameLength();//44100で割ると再生時間（秒）がでる
									long endTime = System.currentTimeMillis()+time*1000/44100;
									clip.start();
									while(true){
											if(stopFlag){//stopFlagがtrueになった終了
													clip.stop();
													return;
											}
											if(endTime < System.currentTimeMillis()){//曲の長さを過ぎたら終了
													if(loopFlag) {
															clip.loop(1);//無限ループとなる
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
			//オオカミはtrue
			return true;
		} else {
			//あかずきんはfalse
			return false;
		}
	}

	//現在自分のターンならtureを返す関数
	public boolean getWhichTurn(){
		Icon whichTurn = turnLabel.getIcon();
		if(whichTurn.equals(myturnIcon)){
			return true;
		} else {
			return false;
		}
	}

	//ファイルパスを引数にする
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

	//連続パスの禁止
	public void setPass(){
		if(passCount > 0) {
			passCount++;
		}
		if(passCount == 4){
			passCount = 0;
		}
	}

	//ストーリーを進める場所です
	public void tellStory(int tCon){

		switch(tCon){
		case 1:

			//共通部分
			strow1.setText("むかし、それまでに");
			strow2.setText("誰も見たことがない");
			strow3.setText("ほどきれいな");
			strow4.setText("女の子がいました。");

			//それぞれのコメント
			if(getWolfOrRed()){
				//オオカミ
				comrow1.setText("オレの出番はまだか");
				comrow2.setText("");
			} else {
				//あかずきん
				comrow1.setText("ワタシ美人なの");
				comrow2.setText("");
			}

			break;
		case 2:
			strow1.setText("この子に夢中なおばあさんが");
			strow2.setText("赤いずきんを作らせましたが");
			strow3.setText("それがよく似合ったので");
			strow4.setText("「赤ずきん」と呼ばれました。");

			if(getWolfOrRed()){
				//オオカミ
				comrow1.setText("ハラがへった");
				comrow2.setText("");
			} else {
				//あかずきん
				comrow1.setText("ホントの");
				comrow2.setText("なまえはヒミツ");
			}

			break;
		case 3:
			strow1.setText("ある日、母が女の子にいいました。");
			strow2.setText("「おばあさんが病気だそうだから");
			strow3.setText("どんな具合か見ておいで。");
			strow4.setText("ガレットとバターをもってね」");

			if(getWolfOrRed()){
				//オオカミ
				comrow1.setText("さっさと");
				comrow2.setText("食っちまおう");
			} else {
				//あかずきん
				comrow1.setText("はじめての");
				comrow2.setText("おつかいよね");
			}

			break;
		case 4:
			strow1.setText("赤ずきんちゃんは別の村に住む");
			strow2.setText("おばあさんの所へ向かって");
			strow3.setText("すぐに出かけました。");
			strow4.setText("");

			if(getWolfOrRed()){
				//オオカミ
				comrow1.setText("いよいよだな");
				comrow2.setText("");
			} else {
				//あかずきん
				comrow1.setText("いってきまーす");
				comrow2.setText("");
			}

			break;

		//ここから分岐
		case 5:
			strow1.setText("赤ずきんちゃんが森に入ると");
			strow2.setText("オオカミが出てきます");
			strow3.setText("オオカミはこの子を");
			strow4.setText("食べたくなりました");

			if(getWolfOrRed()){
				//オオカミ
				comrow1.setText("うまそうな");
				comrow2.setText("ムスメだ");
			} else {
				//あかずきん
				comrow1.setText("ちょっとクサイ");
				comrow2.setText("");
			}

			break;

		case 6:
			strow1.setText("どこへ行くのか");
			strow2.setText("家はどこかなどときかれて");
			strow3.setText("赤ずきんは");
			strow4.setText("ありのままを答えます");

			if(getWolfOrRed()){
				//オオカミ
				comrow1.setText("まぁ興味ないが");
				comrow2.setText("");
			} else {
				//あかずきん
				comrow1.setText("へんなひと");
				comrow2.setText("あ、オオカミか");
			}
			break;

		case 7:
			strow1.setText("オオカミは言いました");
			strow2.setText("「オレもばあさんに会いたい。");
			strow3.setText("どっちが先に着くか");
			strow4.setText("競争しよう。」");

			if(getWolfOrRed()){
				//オオカミ
				comrow1.setText("先にばあさんを");
				comrow2.setText("食べてしまおう");
			} else {
				//あかずきん
				comrow1.setText("こうみえて");
				comrow2.setText("足は速いの");
			}
			break;

		case 8:
			strow1.setText("オオカミは近道を");
			strow2.setText("走っていきましたが");
			strow3.setText("赤ずきんちゃんは");
			strow4.setText("遊びながら行きました");

			if(getWolfOrRed()){
				//オオカミ
				comrow1.setText("血のあじがする");
				comrow2.setText("");
			} else {
				//あかずきん
				comrow1.setText("あんなところに");
				comrow2.setText("キレイな蝶が");
			}
			break;

		case 9:
			strow1.setText("オオカミはおばあさんの");
			strow2.setText("家について、");
			strow3.setText("「孫の赤ずきんよ」と");
			strow4.setText("作り声でいいました。");

			if(getWolfOrRed()){
				//オオカミ
				comrow1.setText("ノドがイタイぜ");
				comrow2.setText("");
			} else {
				//あかずきん
				comrow1.setText("こんなところに");
				comrow2.setText("きれいなお花が");
			}
			break;

		case 10:
			strow1.setText("オオカミは家に入り");
			strow2.setText("おばあさんにとびかかると");
			strow3.setText("すぐ食べてしまいました");
			strow4.setText("");

			if(getWolfOrRed()){
				//オオカミ
				comrow1.setText("なつかしい");
				comrow2.setText("味がする");
			} else {
				//あかずきん
				comrow1.setText("そろそろ");
				comrow2.setText("行こうかしら");
			}
			break;

		case 11:
			strow1.setText("しばらくすると");
			strow2.setText("赤ずきんちゃんが来て、");
			strow3.setText("戸をたたきます。");
			strow4.setText("");

			if(getWolfOrRed()){
				//オオカミ
				comrow1.setText("何してたんだ");
				comrow2.setText("アイツ");
			} else {
				//あかずきん
				comrow1.setText("やっと");
				comrow2.setText("ついたわ");
			}
			break;

			///////////////////////////////////////////////////////////////

		case 12:
			strow1.setText("「どなたかね？」");
			strow2.setText("　という太い声がします。");
			strow3.setText("　おばあさんは風邪を");
			strow4.setText("　引いているのでしょうか。");

			if(getWolfOrRed()){
				//オオカミ
				comrow1.setText("さあ");
				comrow2.setText("入って来い");
			} else {
				//あかずきん
				comrow1.setText("あんな声");
				comrow2.setText("だったかしら");
			}
			if(difCounter == 0) {

			} else if(difCounter == 2) {
				if(getWolfOrRed()){
					//オオカミ
				} else {
					//あかずきん
					comrow1.setText("..........");
					comrow2.setText("");
				}
			}
			break;

		case 13:
			strow1.setText("「赤ずきんよ。");
			strow2.setText("　ガレットとバターの壺を");
			strow3.setText("　もってきたの」");
			strow4.setText("　そう言って中に入りました。");
			if(difCounter == 0) {

			} else if(difCounter == 2) {
				strow1.setText("そんなはずはない。");
				strow2.setText("あかずきんは異変に");
				strow3.setText("気が付いていました。");
				strow4.setText("");
			}

			if(getWolfOrRed()){
				//オオカミ
				comrow1.setText("食べるのは");
				comrow2.setText("オマエだよ");
			} else {
				//あかずきん
				comrow1.setText("ふり回したけど");
				comrow2.setText("大丈夫かな");
			}
			if(difCounter == 0) {

			} else if(difCounter == 2) {
				if(getWolfOrRed()){
					//オオカミ
					comrow1.setText("やけに");
					comrow2.setText("静かだな");
				} else {
					//あかずきん
					comrow1.setText("どうすれば...");
					comrow2.setText("");
				}
			}

			break;

		case 14:
			strow1.setText("　オオカミはベッドの下に");
			strow2.setText("　かくれたまま、");
			strow3.setText("「こっちへ来ておばあちゃん");
			strow4.setText("　とお休み」と言いました。");
			if(difCounter == 0) {

			} else if(difCounter == 2) {
				strow1.setText("「森の中に");
				strow2.setText("　忘れものをしたわ」");
				strow3.setText("　そういってあかずきんは");
				strow4.setText("　逃げ出しました。");
			}

			if(getWolfOrRed()){
				//オオカミ
				comrow1.setText("さあ来い");
				comrow2.setText("");
			} else {
				//あかずきん
				comrow1.setText("なんだか");
				comrow2.setText("おかしいけど...");
			}
			if(difCounter == 0) {

			} else if(difCounter == 2) {
				if(getWolfOrRed()){
					//オオカミ
					comrow1.setText("きづかれた？");
					comrow2.setText("");
				} else {
					//あかずきん
					comrow1.setText("いったん");
					comrow2.setText("逃げましょう");
				}
			}
			break;

		case 15:
			strow1.setText("赤ずきんは服を脱ぎ");
			strow2.setText("ベッドの下に入ろうとしますが");
			strow3.setText("おばあさんの姿を見て");
			strow4.setText("とても驚きます。");
			if(difCounter == 0) {

			} else if(difCounter == 2) {
				strow1.setText("あかずきんは");
				strow2.setText("猟師のおじさんを");
				strow3.setText("頼ることにしました。");
				strow4.setText("");
			}

			if(getWolfOrRed()){
				//オオカミ
				comrow1.setText("さすがに");
				comrow2.setText("気が付かれるか？");
			} else {
				//あかずきん
				comrow1.setText("おばあさん...？");
				comrow2.setText("");
			}
			if(difCounter == 0) {

			} else if(difCounter == 2) {
				if(getWolfOrRed()){
					//オオカミ
					comrow1.setText("さて、");
					comrow2.setText("どうしたものか");
				} else {
					//あかずきん
					comrow1.setText("おばあさんが");
					comrow2.setText("食べられたかも");
				}
			}
			break;

		case 16:
			strow1.setText("「おばあちゃん、");
			strow2.setText("　なんて大きな腕なの？」");
			strow3.setText("「おまえを上手に");
			strow4.setText("抱けるようにだよ」");
			if(difCounter == 0) {

			} else if(difCounter == 2) {
				strow1.setText("事情を聞いた");
				strow2.setText("猟師のおじさんと");
				strow3.setText("あかずきんは");
				strow4.setText("再び森へ向かいます。");
			}

			if(getWolfOrRed()){
				//オオカミ
				comrow1.setText("うだうだ");
				comrow2.setText("うるさいやつだ");
			} else {
				//あかずきん
				comrow1.setText("なにかが...");
				comrow2.setText("");
			}
			if(difCounter == 0) {

			} else if(difCounter == 2) {
				if(getWolfOrRed()){
					//オオカミ
					comrow1.setText("（考え中）");
					comrow2.setText("");
				} else {
					//あかずきん
					comrow1.setText("これで安心");
					comrow2.setText("");
				}
			}
			break;

		case 17:
			strow1.setText("「おばあちゃん、");
			strow2.setText("　なんて大きな脚なの？」");
			strow3.setText("「速く走れるようにだよ」");
			strow4.setText("");
			if(difCounter == 0) {

			} else if(difCounter == 2) {
				strow1.setText("いっぽう、");
				strow2.setText("オオカミも疑われて");
				strow3.setText("いることに気が付いて");
				strow4.setText("いました。");
			}

			if(getWolfOrRed()){
				//オオカミ
				comrow1.setText("逃がさんぞ");
				comrow2.setText("");
			} else {
				//あかずきん
				comrow1.setText(".........");
				comrow2.setText("");
			}
			if(difCounter == 0) {

			} else if(difCounter == 2) {
				if(getWolfOrRed()){
					//オオカミ
					comrow1.setText("何か手はないか");
					comrow2.setText("");
				} else {
					//あかずきん
					comrow1.setText("早く助けないと");
					comrow2.setText("");
				}
			}
			break;

		case 18:
			strow1.setText("「おばあちゃん、");
			strow2.setText("　なんて大きな耳なの？」");
			strow3.setText("「よく聞こえるようにだよ」");
			strow4.setText("");
			if(difCounter == 0) {

			} else if(difCounter == 2) {
				strow1.setText("オオカミは");
				strow2.setText("ベッドとは違う場所に");
				strow3.setText("隠れることにしました。");
				strow4.setText("");
			}

			if(getWolfOrRed()){
				//オオカミ
				comrow1.setText("あと少し...");
				comrow2.setText("");
			} else {
				//あかずきん
				comrow1.setText("そうなのかしら");
				comrow2.setText("");
			}
			if(difCounter == 0) {

			} else if(difCounter == 2) {
				if(getWolfOrRed()){
					//オオカミ
					comrow1.setText("ここなら");
					comrow2.setText("気づかれないな");
				} else {
					//あかずきん
					comrow1.setText("もうすぐね");
					comrow2.setText("");
				}
			}
			break;

		case 19:
			strow1.setText("「おばあちゃん、");
			strow2.setText("　なんて大きな目なの？」");
			strow3.setText("「よく見えるようにだよ」");
			strow4.setText("");
			if(difCounter == 0) {

			} else if(difCounter == 2) {
				strow1.setText("戻ってきたあかずきんたち。");
				strow2.setText("家に入りましたが、オオカミの");
				strow3.setText("姿はどこにもありません。");
				strow4.setText("");
			}

			if(getWolfOrRed()){
				//オオカミ
				comrow1.setText("よだれが...");
				comrow2.setText("");
			} else {
				//あかずきん
				comrow1.setText("やっぱり");
				comrow2.setText("何か変だわ");
			}
			if(difCounter == 0) {

			} else if(difCounter == 2) {
				if(getWolfOrRed()){
					//オオカミ
					comrow1.setText("........");
					comrow2.setText("");
				} else {
					//あかずきん
					comrow1.setText("おかしいわね...");
					comrow2.setText("");
				}
			}
			break;

		case 20:
			strow1.setText("「おばあちゃん、");
			strow2.setText("　なんて大きな歯なの？」");
			strow3.setText("「おまえを食べるためさ」");
			strow4.setText("");
			if(difCounter == 0) {
				strow1.setText("「おばあちゃん、");
				strow2.setText("　なんて大きな歯なの？」");
				strow3.setText("「まて、君は誰だい？」");
				strow4.setText("");
			} else if(difCounter == 2) {
				strow1.setText("不審に思った二人が");
				strow2.setText("外へ出たその時...");
				strow3.setText("");
				strow4.setText("");
			}

			if(getWolfOrRed()){
				//オオカミ
				comrow1.setText("");
				comrow2.setText("");
			} else {
				//あかずきん
				comrow1.setText("オオカミ！");
				comrow2.setText("");
			}
			if(difCounter == 0) {
				if(getWolfOrRed()){
					//オオカミ
					comrow1.setText("");
					comrow2.setText("");
				} else {
					//あかずきん
					comrow1.setText("");
					comrow2.setText("");
				}
			} else if(difCounter == 2) {
				if(getWolfOrRed()){
					//オオカミ
					comrow1.setText("");
					comrow2.setText("");
				} else {
					//あかずきん
					comrow1.setText("");
					comrow2.setText("");
				}
			}

			//ディレイ
			try {
				Thread.sleep(1500);
			} catch(InterruptedException e){
					e.printStackTrace();
			}

			//最後のところは抜けて終了処理へ
			//終了処理
			if(getWhichTurn()){
				String msg = "JUDGE";
				//サーバに情報を送る
				out.println(msg);
				out.flush();
				repaint();
			}
			break;

		}
	}

}

//ダイアログ用のクラス
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
			//エンディング処理に必要な画像

			ImageIcon wolf_win = new ImageIcon("icons/wolf_win.png");
			ImageIcon red_win = new ImageIcon("icons/red_win.png");
			ImageIcon draw = new ImageIcon("icons/draw.png");
			ImageIcon wolf_winwin = new ImageIcon("icons/wolf_winwin.png");
			ImageIcon red_winwin = new ImageIcon("icons/red_winwin.png");
			ImageIcon drawdraw  = new ImageIcon("icons/drawdraw.png");
			JLabel main_image = new JLabel();

			//暗転させる
			littleRed.back90.setVisible(true);

			//ディレイ
			try {
				Thread.sleep(500);
			} catch(InterruptedException e){
					e.printStackTrace();
			}

			//7つのエンディング切り替え
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
				//速攻で決着がついた場合
				main_image.setIcon(draw);
			}

			if(littleRed.difCounter != 3){
			//終了のボタン
			c.add(endbtn);
			endbtn.setBounds(490,290,100,100);
			endbtn.setOpaque(true);
			endbtn.addMouseListener(this);
			endbtn.setContentAreaFilled(false);
			endbtn.setVisible(true);
			endbtn.setBorder(new LineBorder(new Color(102,102,102,255), 1, true)); //枠の色設定

			eactivebtn.addActionListener(this);
			c.add(eactivebtn);
			eactivebtn.setBounds(490,290,100,100);
			eactivebtn.setOpaque(true);
			eactivebtn.addMouseListener(this);
			eactivebtn.setContentAreaFilled(false);
			eactivebtn.setVisible(false);
			eactivebtn.setBorder(new LineBorder(new Color(102,102,102,255), 1, true)); //枠の色設定

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

			setModal(true);//上を閉じるまで下を触れなくする（falseにすると触れる）

			//ダイアログの大きさや表示場所を変更できる
			//親のダイアログの中心に表示したい場合は，親のウィンドウの中心座標を求めて，子のダイアログの大きさの半分ずらす
			setLocation(owner.getBounds().x+owner.getWidth()/2-this.getWidth()/2,owner.getBounds().y+owner.getHeight()/2-this.getHeight()/2);
			setVisible(true);
		}

		//ボタンが押されたときの処理
    public void actionPerformed(ActionEvent e) {
			String str = e.getActionCommand();
			if(str.equals("start")){
				littleRed.op = 0;
			} else if(str.equals("end")){
				littleRed.op = 1;
			}
			this.dispose();//Dialogを廃棄する
		}

		public void mouseEntered(MouseEvent e) {
			JButton theButton = (JButton)e.getComponent();
			Icon theIcon = theButton.getIcon();
			//アイコンのきりかえ
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
			//アイコンの切り替え
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
