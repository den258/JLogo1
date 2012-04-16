package example01;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

class Point {

	public Integer intX = 0;
	public Integer intY = 0;

	public Point() {
		this.intX = 0;
		this.intY = 0;
	}

	public Point(Integer intX, Integer intY) {
		this.intX = intX;
		this.intY = intY;
	}

}

class Command {

	String strCommand = new String();
	Integer intValue = 0;
	Integer intLoopCount = 0;

	public Command(String strArgCommand, Integer intArgValue) {
		this.strCommand = strArgCommand;
		this.intValue = intArgValue;
	}

}

class Utils {

	public static Boolean isNumeric(String strValue) {

		try {
			Integer.parseInt(strValue);
		} catch (Exception objExp) {
			return false;
		}
		return true;

	}

	public static Integer getNumeric(String strValue) {

		if (isNumeric(strValue) == true) {
			return Integer.parseInt(strValue);
		} else {
			return 0;
		}

	}

	public static Double getRadian(Double dblDegree) {
		return dblDegree * (Math.PI / 180);
	}

	public static String getString(Integer intValue) {
		return intValue.toString();
	}

	public static Integer getInteger(Double dblValue) {
		return dblValue.intValue();
	}

}

class JLogo {

	private final String sLINE_END = ";";

	private Point objCurrentPos = new Point();
	private Integer intDegree = 0;
	private Integer intDistance = 0;

	private List<Point> objPoints = new ArrayList<Point>();

	private List<String> objKeywords = new ArrayList<String>();
	public List<Command> objCommands = new ArrayList<Command>();

	public JLogo() {

		this.objKeywords.add("forward");
		this.objKeywords.add("back");
		this.objKeywords.add("right");
		this.objKeywords.add("left");
		this.objKeywords.add("loop");
		this.objKeywords.add("end");

	}

	/***
	 * コマンドの後ろに値(数値)が指定されているか？検査し、objCommands を返す。
	 * @param objSentences
	 * @return
	 * @throws Exception
	 */
	public List<Command> getCommandList(List<String> objSentences) throws Exception {

		List<Command> objReturnList = new ArrayList<Command>();

		for (String strSentence : objSentences) {

			String strValue = null;
			String strCommand = null;

			try {

				if (strSentence.startsWith("end") == true) {

					strCommand = "end";

					strValue = "0";

				} else {

					strCommand =
							strSentence.substring(
									0,
									strSentence.indexOf(" "));

					strValue =
							strSentence.substring(
									strSentence.indexOf(" ") + 1,
									strSentence.indexOf(sLINE_END));

				}

			} catch (Exception objExp) {
				throw new Exception();
			}

			if (Utils.isNumeric(strValue.trim()) == false) {
				throw new Exception();
			}

			objReturnList.add(
					new Command(
							strCommand,
							Utils.getNumeric(strValue)));

		}

		return objReturnList;

	}

	/***
	 * loop の内側の行数のカウント(二重ループ非対応)
	 * @param objArgCommands
	 * @return
	 * @throws Exception
	 */
	public List<Command> getLineNumberInLoop(List<Command> objArgCommands) throws Exception {

		Boolean blnInLoop = false;
		Integer intIndex = 0;
		Integer intLoopCount = 0;
		Integer intIndexOfLoop = 0;

		while(intIndex < objArgCommands.size()) {

			Command objCommand = objArgCommands.get(intIndex);

			if (objCommand.strCommand.equals("end") == true) {

				if (intLoopCount == 0) {
					throw new Exception();
				}

				blnInLoop = false;

				((Command)objArgCommands.get(intIndexOfLoop)).intLoopCount =
						intLoopCount;

			}

			if (blnInLoop == true) {
				intLoopCount++;
			}

			if (objCommand.strCommand.equals("loop") == true) {
				intIndexOfLoop = intIndex;
				blnInLoop = true;
			}

			intIndex++;

		}

		return objArgCommands;

	}

	/***
	 * loop で囲われた行を返す。
	 * @return
	 */
	public List<Command> getCommandsLoopIn(List<Command> objArgCommands) {

		List<Command> objReturnCommands = new ArrayList<Command>();

		Boolean blnInLoop = false;
		Integer intIndex = 0;

		while(intIndex < objArgCommands.size()) {

			Command objCommand = objArgCommands.get(intIndex);

			if (objCommand.strCommand.equals("end") == true) {
				blnInLoop = false;
				break;
			}

			if (blnInLoop == true) {
				objReturnCommands.add(objCommand);
			}

			if (objCommand.strCommand.equals("loop") == true) {
				blnInLoop = true;
			}

			intIndex++;

		}

		return objReturnCommands;

	}

	/***
	 * loop end で囲われた命令行を loop の回数分、コピーする。
	 * @param objArgCommands
	 * @return
	 * @throws Exception
	 */
	public List<Command> expLoop(List<Command> objArgCommands) throws Exception {

		List<Command> objReturnCommands = new ArrayList<Command>();
		List<Command> objInnerLoopCommands = getCommandsLoopIn(objArgCommands);

		Boolean blnIsLoop = false;

		for (Command objCommand : objArgCommands) {

			if (blnIsLoop == true) {
				if (objCommand.strCommand.equals("end") == false) {
					continue;
				}
			}

			if (objCommand.strCommand.equals("loop")) {

				Integer intIndex = 0;

				while(intIndex <= objCommand.intValue) {

					objReturnCommands.addAll(objInnerLoopCommands);

					intIndex++;

				}

				blnIsLoop = true;

				continue;

			}

			if (objCommand.strCommand.equals("end") == true) {
				blnIsLoop = false;
				continue;
			}

			objReturnCommands.add(objCommand);

		}

		return objReturnCommands;

	}

	public void validate(String strArgProgram) throws Exception {

		List<String> objSentences = new ArrayList<String>();

		for (String strSentence : strArgProgram.split(";")) {
			if (strSentence.trim().length() != 0) {
				objSentences.add(strSentence.trim() + ";");
			}
		}

		/**
		 * 行が ";" セミコロンで終わっているかどうか？
		 */
		for (String strSentence : objSentences) {

			if (strSentence.endsWith(sLINE_END) == false) {
				throw new Exception();
			}

		}

		/**
		 * 行が ";" のみかどうか？
		 */
		for (String strSentence : objSentences) {

			if (strSentence.equals(sLINE_END)) {
				throw new Exception();
			}

		}

		/**
		 * 一行に複数のコマンドが含まれていないか？
		 */
		for (String strSentence : objSentences) {

			Integer intMatchCount = 0;

			for (String strKeyword : this.objKeywords) {

				if (strSentence.indexOf(strKeyword) != -1) {
					intMatchCount++;
				}
			}

			if (intMatchCount > 2) {
				throw new Exception();
			}

		}

		/**
		 * コマンドの後ろに値(数値)が指定されているか？
		 * loop の内側の行数のカウント(二重ループ非対応)
		 * loop の展開(未完成)
		 */
		this.objCommands =
				expLoop(
						getLineNumberInLoop(
								getCommandList(
										objSentences)));

	}

	public List<Point> execute() {

		this.objPoints.clear();

		this.objPoints.add(objCurrentPos);

		for (Command objCommand : this.objCommands) {

			Boolean blnAddPoint = false;

			if(objCommand.strCommand.equals("forward")) {

				intDistance = objCommand.intValue;

				blnAddPoint = true;

			}

			if(objCommand.strCommand.equals("back")) {

				intDistance = objCommand.intValue * -1;

				blnAddPoint = true;

			}

			if(objCommand.strCommand.equals("right")) {

				intDegree = intDegree + objCommand.intValue;

			}

			if(objCommand.strCommand.equals("left")) {

				intDegree = intDegree - objCommand.intValue;

			}

			if(objCommand.strCommand.equals("loop")) {

			}

			if(objCommand.strCommand.equals("end")) {

			}

			Point objPoint = new Point();

			objPoint.intX =
					objCurrentPos.intX
					+ Utils.getInteger(
							intDistance.doubleValue() * Math.cos(
									Utils.getRadian(
											intDegree.doubleValue()
									)
					));

			objPoint.intY =
					objCurrentPos.intY
					+ Utils.getInteger(
							intDistance.doubleValue() * Math.sin(
									Utils.getRadian(
											intDegree.doubleValue()
									)
					));

			if (blnAddPoint == true) {

				this.objCurrentPos = objPoint;

				this.objPoints.add(objPoint);

			}

		}

		return this.objPoints;

	}

}

class MyPanel extends JPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private List<Point> objPoints = new ArrayList<Point>();

	/**
	 *
	 */
	@Override
	public void paint(Graphics g) {

		g.setColor(new Color(255, 255, 255));
		g.fillRect(5, 5, 772, 387);

		g.setColor(new Color(200, 200, 200));
		g.drawRect(5, 5, 772, 384);

		g.setColor(new Color(0, 0, 0));

		Point objBeforePoint = null;

		for (Point objPoint : objPoints) {
			objPoint.intX += 386;
			objPoint.intY += 193;
		}

		g.drawRect(- 5, - 5, 10, 10);

		for (Point objPoint : objPoints) {

			if (objBeforePoint != null) {
				g.drawLine(
						objBeforePoint.intX,
						objBeforePoint.intY,
						objPoint.intX,
						objPoint.intY);
			}

			objBeforePoint = objPoint;

			System.out.println("objBeforePoint.intX = " + Utils.getString(objBeforePoint.intX - 5));
			System.out.println("objBeforePoint.intY = " + Utils.getString(objBeforePoint.intY - 5));

			g.drawRect(
					objBeforePoint.intX - 5,
					objBeforePoint.intY - 5,
					10,
					10);

		}


	}

	public void setPoints(List<Point> objArgPoints) {
		this.objPoints = objArgPoints;
	}

}

class MyExecProgramButtonMouseListener implements MouseListener {

	private MyPanel objMyPanel = null;
	private JTextArea objTextArea = null;

	public MyExecProgramButtonMouseListener(
			MyPanel objMyPanel, JTextArea objTextArea) {
		this.objMyPanel = objMyPanel;
		this.objTextArea = objTextArea;
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		JLogo objLogo = new JLogo();

		try {
			objLogo.validate(objTextArea.getText());
		} catch (Exception objExp) {
			objExp.printStackTrace();
		}

		objMyPanel.setPoints(objLogo.execute());
		objMyPanel.repaint();

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO 自動生成されたメソッド・スタブ
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO 自動生成されたメソッド・スタブ
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO 自動生成されたメソッド・スタブ
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO 自動生成されたメソッド・スタブ
	}

}

public class MainClass extends JFrame {

	/**
	 * @see
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	MyPanel objMyPanel = null;
	JButton objButton = null;
	JTextArea objTextArea = null;
	JScrollPane objScrollPane = null;

	/**
	 * @see コンストラクタ
	 */
	public MainClass() {

		this.setLayout(null);

		objMyPanel = new MyPanel();
		objMyPanel.setBounds(5, 5, 780, 390);
		this.add(objMyPanel);

		objButton = new JButton("プログラムを実行");
		objButton.setBounds(633, 400, 150, 45);
		this.add(objButton);

		objTextArea = new JTextArea();

		String strInitialProgram = new String();
		strInitialProgram += "loop 35;\r\n";
		strInitialProgram += "  forward 10;\r\n";
		strInitialProgram += "  right 10;\r\n";
		strInitialProgram += "end;\r\n";

		objTextArea.setText(strInitialProgram);

		objScrollPane =
				new JScrollPane(objTextArea);
		objScrollPane.setBounds(5, 450, 780, 115);

		this.add(objScrollPane);

		objButton.addMouseListener(
				new MyExecProgramButtonMouseListener(
						objMyPanel, objTextArea));

	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

//		String strProgram = new String();
//		JLogo objLogo = new JLogo();

		/**
		 * テストコード 001
		 */

//		strProgram = "forward 100;";
//
//		objLogo = new JLogo();
//
//		try {
//			objLogo.validate(strProgram);
//		} catch (Exception objExp) {
//			System.err.println("プログラムの構文チェックで例外が発生しました。");
//			throw new Exception();
//		}

		/**
		 * テストコード 002
		 */

//		strProgram = new String();
//		strProgram += "loop 2;";
//		strProgram += "  forward 10;";
//		strProgram += "  right 30;";
//		strProgram += "end;";
//
//		objLogo = new JLogo();
//
//		try {
//			objLogo.validate(strProgram);
//		} catch (Exception objExp) {
//			System.err.println("プログラムの構文チェックで例外が発生しました。");
//			throw new Exception();
//		}
//
//		Command objCommand = null;
//
//		if (objLogo.objCommands.size() != 4) {
//			System.err.println("プログラムに誤りがあります。");
//			throw new Exception();
//		}
//
//		objCommand = objLogo.objCommands.get(0);
//		if (objCommand.strCommand.equals("forward") == false ||
//				objCommand.intValue != 10) {
//			System.err.println("プログラムに誤りがあります。");
//			throw new Exception();
//		}
//
//		objCommand = objLogo.objCommands.get(1);
//		if (objCommand.strCommand.equals("right") == false ||
//				objCommand.intValue != 30) {
//			System.err.println("プログラムに誤りがあります。");
//			throw new Exception();
//		}
//
//		objCommand = objLogo.objCommands.get(2);
//		if (objCommand.strCommand.equals("forward") == false ||
//				objCommand.intValue != 10) {
//			System.err.println("プログラムに誤りがあります。");
//			throw new Exception();
//		}
//
//		objCommand = objLogo.objCommands.get(3);
//		if (objCommand.strCommand.equals("right") == false ||
//				objCommand.intValue != 30) {
//			System.err.println("プログラムに誤りがあります。");
//			throw new Exception();
//		}

		/**
		 * テストコード 002
		 */

//		strProgram = new String();
//		strProgram += "forward 10;";
//		strProgram += "right 30;";
//		strProgram += "forward 10;";
//		strProgram += "right 30;";
//		strProgram += "forward 10;";
//		strProgram += "right 30;";
//
//		objLogo = new JLogo();
//
//		try {
//			objLogo.validate(strProgram);
//		} catch (Exception objExp) {
//			System.err.println("プログラムの構文チェックで例外が発生しました。");
//			throw new Exception();
//		}
//
//		if (objLogo.objCommands.size() != 6) {
//			System.err.println("プログラムに誤りがあります。");
//			throw new Exception();
//		}
//
//		objCommand = objLogo.objCommands.get(0);
//		if (objCommand.strCommand.equals("forward") == false ||
//				objCommand.intValue != 10) {
//			System.err.println("プログラムに誤りがあります。");
//			throw new Exception();
//		}
//
//		objCommand = objLogo.objCommands.get(1);
//		if (objCommand.strCommand.equals("right") == false ||
//				objCommand.intValue != 30) {
//			System.err.println("プログラムに誤りがあります。");
//			throw new Exception();
//		}
//
//		objCommand = objLogo.objCommands.get(2);
//		if (objCommand.strCommand.equals("forward") == false ||
//				objCommand.intValue != 10) {
//			System.err.println("プログラムに誤りがあります。");
//			throw new Exception();
//		}
//
//		objCommand = objLogo.objCommands.get(3);
//		if (objCommand.strCommand.equals("right") == false ||
//				objCommand.intValue != 30) {
//			System.err.println("プログラムに誤りがあります。");
//			throw new Exception();
//		}
//
//		objCommand = objLogo.objCommands.get(4);
//		if (objCommand.strCommand.equals("forward") == false ||
//				objCommand.intValue != 10) {
//			System.err.println("プログラムに誤りがあります。");
//			throw new Exception();
//		}
//
//		objCommand = objLogo.objCommands.get(5);
//		if (objCommand.strCommand.equals("right") == false ||
//				objCommand.intValue != 30) {
//			System.err.println("プログラムに誤りがあります。");
//			throw new Exception();
//		}

		/**
		 * 画面起動
		 */
		MainClass objForm = new MainClass();
		objForm.setSize(800, 600);
		objForm.setVisible(true);

	}

}
