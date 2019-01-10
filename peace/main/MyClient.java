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

public class MyClient extends JFrame implements MouseListener,MouseMotionListener,ActionListener {
	private JButton buttonArray[][];//ボタン用の配列
	private Container c;
	private ImageIcon whiteIcon, redIcon, boardIcon,pactiveIcon,ractiveIcon,
			passIcon, resetIcon, guideIcon, redHoodIcon, wolfIcon, wmicon, rmicon;
	private int myColor;
	private int myTurn = 3; //myTurn==3のとき初期ターンとする。myTurn==0は黒、1は白。
	private ImageIcon myIcon, yourIcon;
	private int flipNum = 0;
	private int TurnCount = 0;
	PrintWriter out;//出力用のライター
	private JButton passButton, resetButton, pactiveButton, ractiveButton;
	private int myIconCount = 2, yourIconCount = 2, countSub =0;
	private String chara = "";
	int guideCount = 0;
	static int winner = 2; //0のとき負け
	SoundPlayer theSoundPlayer1;//どこからでもアクセスできるように，クラスのメンバとして宣言

	//ポインターのアイコン
	ImageIcon pointerIcon = new ImageIcon("icons/arrow.png");
	JLabel pointerLabel = new JLabel(pointerIcon);

	//ポインターのカウント
	JLabel pointcon = new JLabel();

	//ターンを示すアイコン
	ImageIcon myturnIcon = new ImageIcon("icons/yourturn.png");
	ImageIcon rivalTurnIcon = new ImageIcon("icons/rivalturn.png");
	JLabel imturnLabel = new JLabel(myturnIcon);

	//ログ
	JLabel strow1 = new JLabel();
	JLabel strow2 = new JLabel();
	JLabel strow3 = new JLabel();
	JLabel strow4 = new JLabel();

	JLabel comrow1 = new JLabel();
	JLabel comrow2 = new JLabel();

	public MyClient() {

		/*
		//名前の入力ダイアログを開く
		String myName = JOptionPane.showInputDialog(null,"名前を入力してください","名前の入力",JOptionPane.QUESTION_MESSAGE);
		if(myName.equals("")){
			myName = "No name";//名前がないときは，"No name"とする
		}
		*/

		String myName = "No name";

		//IPアドレスの入力
		String myIp = JOptionPane.showInputDialog(null,"IPアドレスを入力してください","IPアドレスの入力",JOptionPane.QUESTION_MESSAGE);
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

				c.add(imturnLabel);
				imturnLabel.setBounds(180,540,126,36);
				//imturnLabel.setOpaque(true);
				imturnLabel.setIcon(rivalTurnIcon);

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

				} else {
					myColor = 1;
					myIcon = redIcon;
					yourIcon = whiteIcon;
					setTurn();

					//あなたはあかずきん
					redHoodLab.setBounds(180,410,125,125);
					redminilab.setBounds(365,10,50,50);

					//あなたはオオカミ
					wolfLab.setBounds(30,410,125,125);
					wolfminiLab.setBounds(365,340,50,50);

				}
				//背景画像は最後に定義する
				ImageIcon mainImIcon = new ImageIcon("icons/main-frame.jpg");
				JLabel mainImLabel = new JLabel(mainImIcon);
				c.add(mainImLabel);
				mainImLabel.setBounds(0,0,800,600);
				//mainImLabel.setOpaque(true);

				while(true) {
					String inputLine = br.readLine();//データを一行分だけ読み込んでみる
					if (inputLine != null) {//読み込んだときにデータが読み込まれたかどうかをチェックする
						System.out.println(inputLine);//デバッグ（動作確認用）にコンソールに出力する
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

						} else if(cmd.equals("RESET")) {
							//リセットボタンが押されたときの処理
							
							//ダイアログのテスト
							testDialog();
						} else if(cmd.equals("JUDGE")){
							int win = Integer.parseInt(inputTokens[1]);//guideCount
							testDialog();
							
						} else if(cmd.equals("GUIDE")){
							int theGuide = Integer.parseInt(inputTokens[1]);//guideCount
							guideCount = theGuide; //ガイドの数を両方に適応、共有する
							//System.out.println("guideCount = " + guideCount);

							//置けなければ勝敗判定
							whichWin();
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
		MyClient net = new MyClient();
		net.setVisible(true);
	}

	public void mouseClicked(MouseEvent e) {//ボタンをクリックしたときの処理
		System.out.println("クリックしました"); //デバック
		JButton theButton = (JButton)e.getComponent();//クリックしたオブジェクトを得る．型が違うのでキャストする
		Icon theIcon = theButton.getIcon();//theIconには，現在のボタンに設定されたアイコンが入る

		if(theIcon.equals(boardIcon) || theIcon.equals(guideIcon)){
			String theArrayIndex = theButton.getActionCommand();//ボタンの配列の番号を取り出す
			int temp = Integer.parseInt(theArrayIndex);
			int tempx = temp / 8;
			int tempy = temp % 8;
			//System.out.println("tempx = " + tempx + "tempy = " + tempy);
			if(judgeButton(tempy, tempx)){
				//おける
				Icon whichTurn = imturnLabel.getIcon();
				if(whichTurn.equals(myturnIcon)){
					String msg = "PLACE" + " " + theArrayIndex + " " + myColor + " " + theIcon;
					//サーバに情報を送る
					out.println(msg);
					out.flush();
					repaint();
				} else {
					System.out.println("相手のターンには置けません");
				}

			} else {
				//置けない
			}
			repaint();//画面のオブジェクトを描画し直す

		} else if(theIcon.equals(pactiveIcon)){ //passではなくpactiveに注意
				Icon whichTurn = imturnLabel.getIcon();
				String msg = "PASS" + " " + myTurn + " " + myColor;
				//サーバに情報を送る
				if(whichTurn.equals(myturnIcon)){
				out.println(msg);
				out.flush();
				repaint();
				} else {
					System.out.println("相手のターンにはパスできません");
				}

		} else if(theIcon.equals(ractiveIcon)){
				String msg = "RESET"+ " " + myColor;
				//サーバに情報を送る
				out.println(msg);
				out.flush();
				repaint();

				/*
				////////////////サウンドのテスト/////////////////////////////////////////
				theSoundPlayer1 = new SoundPlayer("sounds/kot.wav");
				theSoundPlayer1.SetLoop(false);//ＢＧＭとして再生を繰り返す
				theSoundPlayer1.play();
				///////////////////////////////////////////////////////////////////////

				///////////timerのテスト////////////////////////////////////////////////
				Timer timer = new Timer(false);
				TimerTask task = new TimerTask() {

					int cnt=0;

					@Override
					public void run() {
						System.out.println("てすと");
						cnt++;
						//5回実行で停止
						if ( cnt >= 5 ) timer.cancel();
					}
				};
				timer.schedule(task, 0, 1000);
				////////////////////////////////////////////////////////////////////////
				*/

		}
	}

	public void mouseEntered(MouseEvent e) {//マウスがオブジェクトに入ったときの処理
		//System.out.println("マウスが入った");

		JButton theButton = (JButton)e.getComponent();//クリックしたオブジェクトを得る．型が違うのでキャストする
		Icon theIcon = theButton.getIcon();//theIconには，現在のボタンに設定されたアイコンが入る

		if(theIcon.equals(boardIcon) || theIcon.equals(guideIcon) || theIcon.equals(redIcon) ||theIcon.equals(whiteIcon)){

		///////////////////////////////////////////////////////////////////////
		theSoundPlayer1 = new SoundPlayer("sounds/kot.wav");
		theSoundPlayer1.SetLoop(false);//ＢＧＭとして再生を繰り返す
		theSoundPlayer1.play();

		///////////////////////////////////////////////////////////////////////
		} else	if (theIcon.equals(passIcon)){
			//重ねておいて表示・非表示を切り替える
			passButton.setVisible(false);
			pactiveButton.setVisible(true);
		} else if (theIcon.equals(resetIcon)){
			//重ねておいて表示・非表示を切り替える
			resetButton.setVisible(false);
			ractiveButton.setVisible(true);
		}
	}

	public void mouseExited(MouseEvent e) {//マウスがオブジェクトから出たときの処理
		//System.out.println("マウス脱出");

		JButton theButton = (JButton)e.getComponent();//クリックしたオブジェクトを得る．型が違うのでキャストする
		Icon theIcon = theButton.getIcon();//theIconには，現在のボタンに設定されたアイコンが入る

		if(theIcon.equals(boardIcon) || theIcon.equals(guideIcon) || theIcon.equals(redIcon) ||theIcon.equals(whiteIcon)){

		} else	if (theIcon.equals(pactiveIcon)){
			//System.out.println("出た");
			passButton.setVisible(true);
			pactiveButton.setVisible(false);
		} else if (theIcon.equals(ractiveIcon)){
			//System.out.println("出た");
			resetButton.setVisible(true);
			ractiveButton.setVisible(false);
		}
	}

	public void mousePressed(MouseEvent e) {//マウスでオブジェクトを押したときの処理（クリックとの違いに注意）
		//System.out.println("マウスを押した");
	}

	public void mouseReleased(MouseEvent e) {//マウスで押していたオブジェクトを離したときの処理
		//System.out.println("マウスを放した");
	}

	public void mouseDragged(MouseEvent e) {//マウスでオブジェクトとをドラッグしているときの処理
	}

	public void mouseMoved(MouseEvent e) {//マウスがオブジェクト上で移動したときの処理
	}

	public boolean judgeButton(int y, int x){
		//System.out.println("judgeButtonが呼ばれました"); //デバック
		boolean flag = false;
		Icon IconComp;
			for(int i=-1; i<=1; i++){
				for(int j=-1; j<=1; j++){
					if(flipButtons(y, x, j, i) >= 1){ //一つ以上裏返せる場合
						IconComp = buttonArray[y+j][x+i].getIcon();
						//System.out.println("y+j="+(y+j)+", x+i="+(x+i));
						flag = true;

						for(int dy=j, dx=i, k=0; k<flipNum; k++, dy+=j, dx+=i){
							//ボタンの位置情報を作る
							int msgy = y + dy;
							int msgx = x + dx;
							int theArrayIndex = msgx*8 + msgy;
							Icon whichTurn = imturnLabel.getIcon();

							//自分のターンのときだけフリップ送信
							if(whichTurn.equals(myturnIcon)){
								String msg = "FLIP"+" "+theArrayIndex+" "+myColor;
								out.println(msg);
								out.flush();
								repaint();
							} else {
								System.out.println("相手のターンには裏返せません");
							}
						}
					} else { //ひとつも裏返せない
						//System.out.println("ひとつも裏返せない"); //デバック
					}
				}
			}
			return flag;
	}

	public int flipButtons(int y, int x, int j, int i){
		//System.out.println("flipButtonsが呼ばれました"); //デバック
		Icon IconRev;
		flipNum = 0; //初期化
		for(int dy=j, dx=i; ; dy+=j, dx+=i) { //終了条件が空です
			try{
				IconRev = buttonArray[(y+dy)][(x+dx)].getIcon();
				//System.out.println("y+dy = " + (y+dy) + " " + "x+dx = " + (x+dx)); //デバック
				//System.out.println("IconRev = " + IconRev); //デバック
				if(dy == 0){
					if(dx == 0){
						flipNum = 0;
						break;
					}
				}

				if(IconRev.equals(boardIcon)){
				//System.out.println("その方向には緑があるよ");
				flipNum = 0;
				break;
				} else if(IconRev.equals(myIcon)) {
					//System.out.println("その方向には君の色があるよ");
					break;
				} else if(IconRev.equals(yourIcon)){
					//System.out.println("まだ進めるよ");
					flipNum++;
				} else if(IconRev.equals(guideIcon)){
					flipNum = 0;
					break;
				}
			}catch(ArrayIndexOutOfBoundsException e){
				//System.out.println("その方向には盤面がありません");
				flipNum = 0;
				break;
			}
		}
		return flipNum;
	}

	public static void setFlame() {
		JFrame frame = new JFrame();
		// タイトル名を設定
		frame.setTitle( "背景色を赤色に" );
		// フレームの大きさを設定
		frame.setSize( 400, 320 );
		// ”×”ボタンを押した時の処理を設定
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		// 背景色の変更
		frame.getContentPane().setBackground( Color.RED );
		// フレームを表示
		frame.setVisible( true );
	}

	///////////////////////////////////////////UIの生成////////////////////////////
	public void setUp(){
		//ウィンドウを作成する
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//ウィンドウを閉じるときに，正しく閉じるように設定する
		setTitle("MyClient");//ウィンドウのタイトルを設定する
		setSize(812,635);//ウィンドウのサイズを設定する
		//getContentPane().setBackground( Color.decode("#685653")); //背景色の変更
		c = getContentPane();//フレームのペインを取得する

		//アイコンの設定
		redIcon = new ImageIcon("icons/r-icon.png");
		whiteIcon = new ImageIcon("icons/w-icon.png");
		boardIcon = new ImageIcon("icons/b-icon.png");
		passIcon = new ImageIcon("icons/pass.png");
		pactiveIcon = new ImageIcon("icons/pactive.png");
		resetIcon = new ImageIcon("icons/reset.png");
		ractiveIcon= new ImageIcon("icons/reset-active.png");
		guideIcon = new ImageIcon("icons/g-icon.png");

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
			//buttonArray[i][j].setBorderPainted(false); //線の透明化
		  }
		}

		buttonArray[3][3].setIcon(redIcon);
		buttonArray[4][3].setIcon(whiteIcon);
		buttonArray[3][4].setIcon(whiteIcon);
		buttonArray[4][4].setIcon(redIcon);

		//ポインターカウント ほかから使うので外で宣言
		c.add(pointcon);
		pointcon.setBounds(398,180,30,30);
		pointcon.setText(" " + Integer.toString(countSub));
		pointcon.setForeground(Color.decode("#c0bfbf"));
		//pointcon.setOpaque(true); //背景透明化
		pointcon.setFont(new Font("UD デジタル 教科書体 N-B", Font.PLAIN, 18));

		//ポインター ほかから使うので外で宣言
		c.add(pointerLabel);
		pointerLabel.setBounds(360-40+13,200-70+35,60,60);
		//pointerLabel.setOpaque(true);

		//ログの中身 ほかでも使うので先頭で定義。
		//JLabel strow1 = new JLabel();
		c.add(strow1);
		strow1.setBounds(20,-10,300,300);
		strow1.setText("初期のテキストです。初期のテキスト。");
		strow1.setFont(new Font("UD デジタル 教科書体 N-B", Font.PLAIN, 16));
		strow1.setForeground(new Color(192,191,191,255));

		//JLabel strow2 = new JLabel();
		c.add(strow2);
		strow2.setBounds(20,50,300,300);
		strow2.setText("初期のテキストです。初期のテキスト。");
		strow2.setFont(new Font("UD デジタル 教科書体 N-B", Font.PLAIN, 16));
		strow2.setForeground(new Color(192,191,191,255));

		//JLabel strow3 = new JLabel();
		c.add(strow3);
		strow3.setBounds(20,110,300,300);
		strow3.setText("初期のテキストです。初期のテキスト。");
		strow3.setFont(new Font("UD デジタル 教科書体 N-B", Font.PLAIN, 16));
		strow3.setForeground(new Color(192,191,191,255));

		//JLabel strow4 = new JLabel();
		//ここはMAX15文字
		c.add(strow4);
		strow4.setBounds(20,170,300,300);
		strow4.setText("初期のテキストです。初期。");
		strow4.setFont(new Font("UD デジタル 教科書体 N-B", Font.PLAIN, 16));
		strow4.setForeground(new Color(192,191,191,255));

		c.add(comrow1);
		comrow1.setBounds(388,412,200,100);
		comrow1.setText("こめんとだお");
		comrow1.setFont(new Font("UD デジタル 教科書体 N-B", Font.PLAIN, 16));
		comrow1.setForeground(new Color(192,191,191,255));

		c.add(comrow2);
		comrow2.setBounds(388,452,200,100);
		comrow2.setText("にぎょうめだお");
		comrow2.setFont(new Font("UD デジタル 教科書体 N-B", Font.PLAIN, 16));
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
		//passButton.setBorderPainted(false);

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

		//resetボタン
		resetButton = new JButton(resetIcon);
		c.add(resetButton);
		resetButton.setBounds(680,390,100,100);
		resetButton.setOpaque(true);
		resetButton.addMouseListener(this);
		resetButton.setContentAreaFilled(false);
		resetButton.setVisible(true);
		resetButton.setBorder(new LineBorder(new Color(102,102,102,255), 1, true)); //枠の色設定
		//resetButton.setBorderPainted(false);

		//アクティブ状態
		ractiveButton = new JButton(ractiveIcon);
		c.add(ractiveButton);
		ractiveButton.setBounds(680,390,100,100);
		ractiveButton.setOpaque(true);
		ractiveButton.addMouseListener(this);
		ractiveButton.setContentAreaFilled(false);
		ractiveButton.setVisible(false);
		ractiveButton.setBorder(new LineBorder(new Color(102,102,102,255), 1, true)); //枠の色設定
		//resetButton.setBorderPainted(false);
		
		//ダイアログのテスト
		//ractiveButton.setActionCommand("PUSH_Dialog");//ボタンをクリックしたときにactionPerformedのtheCmdで受けとる文字列
        //ractiveButton.addActionListener(this);//ボタンをクリックしたときにactionPerformedで受け取るため

	}
	//////////////////////////////////////////////////////////////////////////////

	//リセットの処理
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

		countSub = my - your; //自分が多いと＋
		System.out.println("///////countsub = ////////" + countSub);

		//移送距離は 基準値 + 差分*9
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

	public void setTurn(){
		if(myTurn == 3){
			//////初回の処理 いじらなくてok////////////////////////////////////////////
			if(myIcon.equals(whiteIcon)){
				imturnLabel.setIcon(myturnIcon);
				//あなたのアイコンのすぐ下に
				imturnLabel.setBounds(180,540,126,36);
				//初回のガイド表示
				guide();
			} else {
				imturnLabel.setIcon(rivalTurnIcon);
				imturnLabel.setBounds(30,540,126,36);
			}
		///////////////////////////////////////////////////////////////////////////
		} else {
			//二回目以降の処理
			Icon whichTurn = imturnLabel.getIcon();
			//System.out.println(whichTurn);
			if(whichTurn.equals(myturnIcon)){

				imturnLabel.setLocation(180, 540 );//初期化
				Timer timer = new Timer(false);
				TimerTask task = new TimerTask() {

					int cnt=0;

					@Override
					public void run() {
						imturnLabel.setLocation(imturnLabel.getX() - 3, 540 );
						cnt++;
						//5回実行で停止
						if ( cnt >= 50 ) timer.cancel();
					}
				};
				//初期ディレイとインターバル
				timer.schedule(task, 0, 1);
				imturnLabel.setIcon(rivalTurnIcon);

			} else {

				imturnLabel.setLocation(30, 540 );//初期化
				Timer timer = new Timer(false);
				TimerTask task = new TimerTask() {

					int cnt=0;

					@Override
					public void run() {
						imturnLabel.setLocation(imturnLabel.getX() + 3, 540 );
						cnt++;
						//5回実行で停止
						if ( cnt >= 50 ) timer.cancel();
					}
				};
				//初期ディレイとインターバル
				timer.schedule(task, 0, 1);
				imturnLabel.setIcon(myturnIcon);
			}
		}
	}

	public void guide(){
		//System.out.println("guide();が呼ばれた");
		//初期化
		guideCount = 0;
		Icon IconComp;
		Icon whichTurn = imturnLabel.getIcon();
		if(whichTurn.equals(myturnIcon)){
			for(int i=0; i<8; i++){
				for(int j=0; j<8; j++){
					IconComp = buttonArray[i][j].getIcon();
					//System.out.println(IconComp);
					if(IconComp == boardIcon){
						//緑のとき探索開始
						//System.out.println("ジャッジ開始");
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
						//System.out.println("ガイド消した");
						buttonArray[i][j].setIcon(boardIcon);
					}
				}
			}
		}
	}

	public void judgeButton2(int y, int x){
		//System.out.println("judgeButton2が呼ばれました"); //デバック
		boolean flag = false;
		//Icon IconComp;
			for(int i=-1; i<=1; i++){
				for(int j=-1; j<=1; j++){
					if(flipButtons(y, x, j, i) >= 1){ //一つ以上裏返せる場合
						//System.out.println("flipNum" + flipNum);
						//System.out.println("y = " + y + "x = " + x);
						flag = true;
						break;
					} else { //ひとつも裏返せない
						//System.out.println("ひとつも裏返せない"); //デバック
					}
				}
			}
			if(flag){
				//System.out.println("アイコン変えたよ");
				guideCount++;
				buttonArray[y][x].setIcon(guideIcon);
			}

	}

	public void tellStory(int tCon){

		switch(tCon){
		case 1:
			strow1.setText("むかし、それまでに誰も見たことがない"); //初期設定では一行で20文字です
			strow2.setText("ほどきれいな、女の子がいました。");
			strow3.setText("");
			break;
		case 2:
			strow1.setText("この子に夢中なおばあさんが赤いずきんを");
			strow2.setText("作らせましたが、それがよく似合ったので");
			strow3.setText("どこへ行っても「赤ずきんちゃん」と呼ばれました。");
			break;
		case 3:
			strow1.setText("ある日、母親は女の子にいいました。");
			strow2.setText("「おばあさんが病気だそうだから、どんな具合か");
			strow3.setText("見ておいで。このガレットとバターの壺をもってね」");
			break;
		case 4:
			strow1.setText("赤ずきんちゃんは別の村に住むおばあさんの");
			strow2.setText("所へ向かって、すぐに出かけました。");
			strow3.setText("");
			break;
		//ここから分岐
		case 5:
			if(myIcon == redIcon){
				//赤ずきん視点
				strow1.setText("あなたが森を少し歩いていると、");
				strow2.setText("おおかみが声をかけてきました。");
				strow3.setText("");
			}else {
				//おおかみ視点
				strow1.setText("いつものように「えさ」を探していると");
				strow2.setText("赤いずきんをかぶった女の子を見つけました。");
				strow3.setText("あなたは声をかけることにしました。");
			}
			break;
		case 6:
			if(myIcon == redIcon){
				//赤ずきん視点
				strow1.setText("「どこへ行くの？」");
				strow2.setText("「おばあさんのおうちはどこ？」");
				strow3.setText("");
			}else {
				//おおかみ視点
				strow1.setText("「どこへ行くの？」");
				strow2.setText("「おばあさんのおうちはどこ？」");
				strow3.setText("");
			}
			break;
		}
	}

	public void endTurn(){
		//System.out.println("endTurnが呼ばれた");
		setTurn(); //あなたorあいてのターンです（ラベル切り替え）
		TurnCount++; //ターンカウントを増やす
		comrow1.setText("今は" + TurnCount + "ターン目です"); //ラベルのセット
		guide(); //ガイドの作成+置ける場所の判定（なければ終了）
		tellStory(TurnCount);
	}

	//勝敗判定です
	public void whichWin(){
		if(guideCount == 0){
			countSub = myIconCount - yourIconCount;
			if(countSub > 0){
				comrow1.setText("あなたの勝ち！");
				winner = 1;
			} else if(countSub==0){
				comrow1.setText("引き分け！");
				winner = 2;
			} else {
				comrow1.setText("あなたの負け！");
				winner = 0;
			}
			String msg = "JUDGE"+ " " + winner;
			//サーバに情報を送る
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
									//System.out.println("PlaySound time="+time);
									long endTime = System.currentTimeMillis()+time*1000/44100;
									clip.start();
									//System.out.println("PlaySound time="+(int)(time/44100));
									while(true){
											if(stopFlag){//stopFlagがtrueになった終了
													System.out.println("PlaySound stop by stopFlag");
													clip.stop();
													return;
											}
											//System.out.println("endTime="+endTime);
											//System.out.println("currentTimeMillis="+System.currentTimeMillis());
											if(endTime < System.currentTimeMillis()){//曲の長さを過ぎたら終了
													//System.out.println("PlaySound stop by sound length");
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
					//System.out.println("StopSound");
			}

	}
	
	public void actionPerformed(ActionEvent e) {
        System.out.println("アクション発生");
        System.out.println(e.getSource());
        String theCmd = e.getActionCommand();
        System.out.println("ActionCommand: "+theCmd);

        //theButton1を押したときに，ダイアログを表示する
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

//ダイアログ用のクラス
class WinDialogWindow extends JDialog implements ActionListener{
    WinDialogWindow(JFrame owner) {
        super(owner);//呼び出しもととの親子関係の設定．これをコメントアウトすると別々のダイアログになる

		Container c = this.getContentPane();	//フレームのペインを取得する
        c.setLayout(null);		//自動レイアウトの設定を行わない
		int winner = MyClient.returnWinner();
		System.out.println("winnerの値は" + winner);

        JButton theButton = new JButton();//画像を貼り付けるラベル
		if(winner == 0){
			theButton.setText("あなたのまけ！");
		} else if (winner == 1){
			theButton.setText("あなたのかち！");
		} else {
			theButton.setText("ひきわけ！");
		}
        //ImageIcon theImage = new ImageIcon("win.jpg");//なにか画像ファイルをダウンロードしておく
        //theButton.setIcon(theImage);//ラベルを設定
        theButton.setBounds(0,0,526,234);//ボタンの大きさと位置を設定する．(x座標，y座標,xの幅,yの幅）
        theButton.addActionListener(this);//ボタンをクリックしたときにactionPerformedで受け取るため
        c.add(theButton);//ダイアログに貼り付ける（貼り付けないと表示されない

        setTitle("You Win!");//タイトルの設定
        setSize(526, 234);//大きさの設定
        setResizable(false);//拡大縮小禁止//trueにすると拡大縮小できるようになる
        setUndecorated(true); //タイトルを表示しない
        setModal(true);//上を閉じるまで下を触れなくする（falseにすると触れる）

        //ダイアログの大きさや表示場所を変更できる
        //親のダイアログの中心に表示したい場合は，親のウィンドウの中心座標を求めて，子のダイアログの大きさの半分ずらす
        setLocation(owner.getBounds().x+owner.getWidth()/2-this.getWidth()/2,owner.getBounds().y+owner.getHeight()/2-this.getHeight()/2);
        setVisible(true);
    }
    public void actionPerformed(ActionEvent e) {
        this.dispose();//Dialogを廃棄する
    }
}

