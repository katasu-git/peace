import java.net.*;
import java.io.*;
import javax.swing.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class MyClient extends JFrame implements MouseListener,MouseMotionListener {
	private JButton buttonArray[][];//ボタン用の配列
	private Container c;
	private ImageIcon blackIcon, whiteIcon, boardIcon, passIcon, resetIcon, guideIcon ;
	private int myColor;
	private int myTurn = 3; //myTurn==3のとき初期ターンとする。myTurn==0は黒、1は白。
	private ImageIcon myIcon, yourIcon;
	private int flipNum = 0;
	private int TurnCount = 0;
	PrintWriter out;//出力用のライター
	private JButton passButton;
	private JButton resetButton;
	private int myIconCount = 2, yourIconCount = 2, countSub =0;
	private String chara = "";
	int guideCount = 0;

	//ポインターのアイコン
	ImageIcon pointerIcon = new ImageIcon("images/pointer.png");
	JLabel pointerLabel = new JLabel(pointerIcon);

	//ポインターのカウント
	JLabel pointcon = new JLabel();

	//ターンを示すアイコン
	ImageIcon myturnIcon = new ImageIcon("images/myturn.png");
	ImageIcon yourturnIcon = new ImageIcon("images/yourturn.png");
	JLabel imturnLabel = new JLabel(myturnIcon);

	//ターンカウントの中身
	JLabel tComment = new JLabel();

	//ログの中身
	JLabel logComment = new JLabel();
	JLabel logComment2 = new JLabel();
	JLabel logComment3 = new JLabel();

	public MyClient() {
		//名前の入力ダイアログを開く
		String myName = JOptionPane.showInputDialog(null,"名前を入力してください","名前の入力",JOptionPane.QUESTION_MESSAGE);
		if(myName.equals("")){
			myName = "No name";//名前がないときは，"No name"とする
		}

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
				imturnLabel.setBounds(830,465,200,60);
				imturnLabel.setOpaque(true);
				imturnLabel.setIcon(yourturnIcon);

				if(myNumberInt % 2 == 0){
					myColor = 0;
					myIcon = blackIcon;
					yourIcon = whiteIcon;
					setTurn();

					//あなたのいろ
					ImageIcon myblackIcon = new ImageIcon("images/myblack.png");
					JLabel myblackLabel = new JLabel(myblackIcon);
					c.add(myblackLabel);
					myblackLabel.setBounds(620,465,200,60);
					myblackLabel.setOpaque(true);

				} else {
					myColor = 1;
					myIcon = whiteIcon;
					yourIcon = blackIcon;
					setTurn();

					//あなたのいろ
					ImageIcon mywhiteIcon = new ImageIcon("images/mywhite.png");
					JLabel mywhiteLabel = new JLabel(mywhiteIcon);
					c.add(mywhiteLabel);
					mywhiteLabel.setBounds(620,465,200,60);
					mywhiteLabel.setOpaque(true);

				}

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
							movePointer(myIconCount, yourIconCount);
							endTurn();
							/*setTurn();
							countTurn();*/

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
						} else if(cmd.equals("JUDGE")){
							int theColor = Integer.parseInt(inputTokens[1]);//myColor
							if(theColor == myColor){
								//System.out.println("あなたの負けです");
								tComment.setText("あなたの負けです");
							} else {
								//System.out.println("あなたの勝ちです");
								tComment.setText("あなたの勝ちです");
							}
						} else if(cmd.equals("GUIDE")){
							int theGuide = Integer.parseInt(inputTokens[1]);//guideCount
							guideCount = theGuide; //ガイドの数を両方に適応、共有する
							System.out.println("guideCount = " + guideCount);
							
							//置けなければ勝敗判定
							if(guideCount == 0){
								Icon whichTurn = imturnLabel.getIcon();
								if(whichTurn.equals(myturnIcon)){
									tComment.setText("あなたの負けです");
								} else {
									tComment.setText("あなたの勝ちです");
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
		MyClient net = new MyClient();
		net.setVisible(true);
	}

	public void mouseClicked(MouseEvent e) {//ボタンをクリックしたときの処理
		System.out.println("クリックしました"); //デバック
		JButton theButton = (JButton)e.getComponent();//クリックしたオブジェクトを得る．型が違うのでキャストする
		Icon theIcon = theButton.getIcon();//theIconには，現在のボタンに設定されたアイコンが入る

		//if(theButton.getText().equals("PASS")){
		//	System.out.println("PURESS THE PASS BUTOON");
		//}

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

		} else if(theIcon.equals(passIcon)){
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

		} else if(theIcon.equals(resetIcon)){
				String msg = "RESET"+ " " + myColor;
				//サーバに情報を送る
				out.println(msg);
				out.flush();
				repaint();
		}
	}

	public void mouseEntered(MouseEvent e) {//マウスがオブジェクトに入ったときの処理
		//System.out.println("マウスが入った");
	}

	public void mouseExited(MouseEvent e) {//マウスがオブジェクトから出たときの処理
		//System.out.println("マウス脱出");
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

	public void setUp(){
		//ウィンドウを作成する
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//ウィンドウを閉じるときに，正しく閉じるように設定する
		setTitle("MyClient");//ウィンドウのタイトルを設定する
		setSize(1050,1000);//ウィンドウのサイズを設定する
		c = getContentPane();//フレームのペインを取得する

		//アイコンの設定
		whiteIcon = new ImageIcon("images/White.png");
		blackIcon = new ImageIcon("images/Black.png");
		boardIcon = new ImageIcon("images/GreenFrame.png");
		passIcon = new ImageIcon("images/pass.png");
		resetIcon = new ImageIcon("images/reset.png");
		guideIcon = new ImageIcon("images/guideIcon.png");

		c.setLayout(null);//自動レイアウトの設定を行わない
		//ボタンの生成

		buttonArray = new JButton[8][8];

		for(int j=0;j<8;j++){
			for(int i=0;i<8;i++){
			buttonArray[i][j] = new JButton(boardIcon);//ボタンにアイコンを設定する
			c.add(buttonArray[i][j]);//ペインに貼り付ける

			buttonArray[i][j].setBounds(i*50+620,j*50+10,50,50);//ボタンの大きさと位置を設定する．(x座標，y座標,xの幅,yの幅）
			buttonArray[i][j].addMouseListener(this);//ボタンをマウスでさわったときに反応するようにする
			buttonArray[i][j].addMouseMotionListener(this);//ボタンをマウスで動かそうとしたときに反応するようにする
			buttonArray[i][j].setActionCommand(Integer.toString(j*8+i));//ボタンに配列の情報を付加する（ネットワークを介してオブジェクトを識別するため）
			buttonArray[i][j].setContentAreaFilled(false); //ボタン背景の透明化
			//buttonArray[i][j].setBorderPainted(false); //線の透明化
		  }
		}

		buttonArray[3][3].setIcon(whiteIcon);
		buttonArray[4][3].setIcon(blackIcon);
		buttonArray[3][4].setIcon(blackIcon);
		buttonArray[4][4].setIcon(whiteIcon);


		//メインイメージ
		ImageIcon mainImIcon = new ImageIcon("images/64.jpg");
		JLabel mainImLabel = new JLabel(mainImIcon);
		c.add(mainImLabel);
		mainImLabel.setBounds(10,10,600,400);
		mainImLabel.setOpaque(true);

		//ポインターカウント ほかから使うので外で宣言
		c.add(pointcon);
		pointcon.setBounds(304,500,30,30);
		pointcon.setOpaque(true);
		pointcon.setText(Integer.toString(countSub));
		pointcon.setFont(new Font("MS ゴシック", Font.PLAIN, 24));

		//ポインター ほかから使うので外で宣言
		c.add(pointerLabel);
		pointerLabel.setBounds(282,420,60,60);
		pointerLabel.setOpaque(true);

		//バー
		ImageIcon barIcon = new ImageIcon("images/bar.png");
		JLabel barLabel = new JLabel(barIcon);
		c.add(barLabel);
		barLabel.setBounds(10,465,600,50);
		barLabel.setOpaque(true);

		//ログの中身 ほかでも使うので先頭で定義。
		//JLabel logComment = new JLabel();
		c.add(logComment);
		logComment.setBounds(70,580,510,150);
		logComment.setText("初期のテキストです。初期のテキストです。");
		logComment.setFont(new Font("MS ゴシック", Font.PLAIN, 24));

		//JLabel logComment2 = new JLabel();
		c.add(logComment2);
		logComment2.setBounds(70,620,510,150);
		logComment2.setText("初期のテキストです。初期のテキストです。");
		logComment2.setFont(new Font("MS ゴシック", Font.PLAIN, 24));

		//JLabel logComment3 = new JLabel();
		c.add(logComment3);
		logComment3.setBounds(70,660,510,150);
		logComment3.setText("初期のテキストです。初期のテキストです。");
		logComment3.setFont(new Font("MS ゴシック", Font.PLAIN, 24));

		//ログ
		ImageIcon logIcon = new ImageIcon("images/63.png");
		JLabel logLabel = new JLabel(logIcon);
		c.add(logLabel);
		logLabel.setBounds(10,540,600,300);
		logLabel.setOpaque(true);


		//パスボタン
		passButton = new JButton(passIcon);
		c.add(passButton);
		passButton.setBounds(620,535,200,60);
		passButton.setOpaque(true);
		passButton.addMouseListener(this);

		//ターンカウントの中身
		//JLabel tComment = new JLabel();  ほかでも使うので先頭で定義。
		c.add(tComment);
		tComment.setBounds(630,605,200,60);
		tComment.setText("今は" + TurnCount + "ターン目です");
		tComment.setFont(new Font("MS ゴシック", Font.PLAIN, 20));
		tComment.setForeground(Color.white);

		//ターンカウントの下地
		ImageIcon tConIcon = new ImageIcon("images/count.png");
		JLabel tConBack = new JLabel(tConIcon);
		c.add(tConBack);
		tConBack.setBounds(620,605,200,60);
		tConBack.setOpaque(true);

		//resetボタン
		passButton = new JButton(resetIcon);
		c.add(passButton);
		passButton.setBounds(830,535,200,60);
		passButton.setOpaque(true);
		passButton.addMouseListener(this);

	}

	//リセットの処理
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

	}

	public void movePointer(int my, int your){
		//220
		//countSub = Math.abs(my - your);
		countSub = my - your;
		pointerLabel.setLocation(282,420);

		//ラベルの位置は微調整してください
		if(countSub == 0){
			pointerLabel.setLocation(282,420);
			pointcon.setText(Integer.toString(countSub));
			pointcon.setLocation((304 + countSub*13),500);
		} else if (countSub > 0){
			pointerLabel.setLocation((282 + countSub*13),420);
			pointcon.setText("+" + Integer.toString(countSub));
			pointcon.setLocation((297 + countSub*13),500);
		} else {
			pointerLabel.setLocation((282 + countSub*13),420);
			pointcon.setText(Integer.toString(countSub));
			pointcon.setLocation((297 + countSub*13),500);
		}
		repaint();
	}

	public void setTurn(){
		//初回の処理
		if(myTurn == 3){
			if(myIcon.equals(blackIcon)){
				imturnLabel.setIcon(myturnIcon);
				//初回のガイド表示
				guide();
			} else {
				imturnLabel.setIcon(yourturnIcon);
			}
		} else {
			//二回目以降の処理
			Icon whichTurn = imturnLabel.getIcon();
			System.out.println(whichTurn);
			if(whichTurn.equals(myturnIcon)){
				imturnLabel.setIcon(yourturnIcon);
			} else {
				imturnLabel.setIcon(myturnIcon);
			}
		}
	}

	public void guide(){
		System.out.println("endTurnが呼ばれた");
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
			String msg = "GUIDE" + " " + guideCount;
			out.println(msg);
			out.flush();
			repaint();
	}

	public void tellStory(int tCon){

		switch(tCon){
		case 1:
			logComment.setText("1ターン目だよ"); //初期設定では一行で20文字です
			logComment2.setText("");
			logComment3.setText("");
			break;
		case 2:
			logComment.setText("2ターン目だよ");
			break;
		case 3:
			logComment.setText("3ターン目だよ");
			break;
		}
	}
	
	public void endTurn(){
		//System.out.println("endTurnが呼ばれた");
		setTurn(); //あなたorあいてのターンです（ラベル切り替え）
		TurnCount++; //ターンカウントを増やす
		tComment.setText("今は" + TurnCount + "ターン目です"); //ラベルのセット
		guide(); //ガイドの作成+置ける場所の判定（なければ終了）
		tellStory(TurnCount);
	}




}
