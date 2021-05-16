package net.dankichi.util;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 和暦を管理するクラスです。<br/>
 * GregorianCalendarを継承しています<br/>
 * <br/>
 * 内部で持つlong値はすべて日本標準時（GMT+9）で判断しています。<br/>
 * ユリウス暦593年2月6日23時59分59秒999以前のデータで和暦情報を取得するとUnsupportedOperationExceptionをthrowします
 * 。<br/>
 * 未来日は令和での換算になります。<br/>
 * 1873年（明治6年）1月1日以降の月日はグレゴリオ暦と同じになります。<br/>
 * 明治以前は改元があった場合、該当年の正月に遡って改元するというルールに準拠しています。<br/>
 * 大正以降の改元は、改元日になります。<br/>
 * TimeZoneは、"Asia/Tokyo"が強制的に設定されます。<br/>
 * <br/>
 * 参考文献：暦の百科事典（新人物往来社）
 *
 */
public class JapaneseCalendar extends GregorianCalendar {

	private static final long serialVersionUID = 4728083532473105244L;

	private boolean isWarekiSet = false;
	/**
	 * 0:GENGO：元号、0（推古）から255（令和）までをコードで返す<br/>
	 * 1:YEAR_OF_GENGO：該当元号の年を返す。元年は1<br/>
	 * 2:YEAR_OF_JP：和暦の年を西暦にして返す。<br/>
	 * 3:MONTH_JP：和暦の月を返す、該当年の最初の月は0、最大12まで<br/>
	 * 4:DAY_OF_MONTH_JP：和暦の月内の日を返す<br/>
	 * 5:NAME_OF_MONTH_JP：和暦の月の名称を返す。正月は1、2月は2・・12月は12、閏正月は21、閏2月は22・・・閏12月は32<br/>
	 * 6:JIKKAN_OF_YEAR：年の十干を返す。0から9まで順番に、甲乙丙丁戊己庚辛壬癸<br/>
	 * 7:JUUNISHI_OF_YEAR：年の十二支を返す。0から11まで順番に、子丑寅卯辰巳午未申酉戌亥<br/>
	 * 8:JIKKAN_OF_DAY：日の十干を返す<br/>
	 * 9:JUUNISHI_OF_DAY：日の十二支を返す<br/>
	 */
	private int[] warekiFields = new int[10];

	// 北朝であるか、デフォルトはfalse（南朝）
	private boolean isNorthImperialCourt = false;

	public static final int GENGO = 900;
	public static final int YEAR_OF_GENGO = 901;
	public static final int YEAR_OF_JP = 902;
	public static final int MONTH_JP = 903; // 0～12
	public static final int DAY_OF_MONTH_JP = 904;
	public static final int NAME_OF_MONTH_JP = 905;

	public static final int JIKKAN_OF_YEAR = 906;
	public static final int JUUNISHI_OF_YEAR = 907;
	public static final int JIKKAN_OF_DAY = 908;
	public static final int JUUNISHI_OF_DAY = 909;
	public static final int SHICHIYOU = 910;

	public JapaneseCalendar() {
		super(TimeZone.getTimeZone("Asia/Tokyo"));
	}

	public JapaneseCalendar(TimeZone timezone) {
		super(TimeZone.getTimeZone("Asia/Tokyo"));
	}

	public JapaneseCalendar(Locale locale) {
		super(TimeZone.getTimeZone("Asia/Tokyo"), locale);
	}

	public JapaneseCalendar(TimeZone timezone, Locale locale) {
		super(TimeZone.getTimeZone("Asia/Tokyo"), locale);
	}

	public JapaneseCalendar(int i, int j, int k) {
		super(i, j, k);
		setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
	}

	public JapaneseCalendar(int i, int j, int k, int l, int i1) {
		super(i, j, k, l, i1);
		setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
	}

	public JapaneseCalendar(int i, int j, int k, int l, int i1, int j1) {
		super(i, j, k, l, i1, j1);
		setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
	}

	public JapaneseCalendar(Calendar calendar) {
		this.setTimeInMillis(calendar.getTimeInMillis());
		setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
	}

	/**
	 * 値取得。和暦に特化した引数は以下<br/>
	 *
	 * @param i
	 * GENGO：元号、0（推古）から255（令和）までをコードで返す<br/>
	 * YEAR_OF_GENGO：該当元号の年を返す。元年は1<br/>
	 * YEAR_OF_JP：和暦の年を西暦にして返す。<br/>
	 * MONTH_JP：和暦の月を返す、該当年の最初の月は0、最大12まで<br/>
	 * DAY_OF_MONTH_JP：和暦の月内の日を返す<br/>
	 * NAME_OF_MONTH_JP：和暦の月の名称を返す。正月は1、2月は2・・12月は12、閏正月は21、閏2月は22・・・閏12月は32<br/>
	 * JIKKAN_OF_YEAR：年の十干を返す。0から9まで順番に、甲乙丙丁戊己庚辛壬癸<br/>
	 * JUUNISHI_OF_YEAR：年の十二支を返す。0から11まで順番に、子丑寅卯辰巳午未申酉戌亥<br/>
	 * JIKKAN_OF_DAY：日の十干を返す<br/>
	 * JUUNISHI_OF_DAY：日の十二支を返す<br/>
	 * @return
	 */
	public int get(int i) {
		if (i < 900 || i > 910)
			// 本クラスで定義しているstaticfinal値以外は、superクラスの挙動に依存
			return super.get(i);

		// 和暦がセットされていなければ和暦作成処理
		if (!isWarekiSet)
			setFields();

		return warekiFields[i - 900];
	}

	/**
	 * 値を日本語表記で取得。引数は以下<br/>
	 *
	 * @param i
	 * GENGO：元号名、西暦593年以降の天皇名はかっこで囲んだもの<br/>
	 * NAME_OF_MONTH_JP：和暦の月の名称を返す。例：「1」「12」「閏1」「閏12」<br/>
	 * JIKKAN_OF_YEAR：年の十干を返す。甲乙丙丁戊己庚辛壬癸のいずれか<br/>
	 * JUUNISHI_OF_YEAR：年の十二支を返す。子丑寅卯辰巳午未申酉戌亥のいずれか<br/>
	 * JIKKAN_OF_DAY：日の十干を返す<br/>
	 * JUUNISHI_OF_DAY：日の十二支を返す<br/>
	 * SHICHIYOU：七曜を返す<br/>
	 * @return
	 */
	public String getString(int i) {
		if (i != GENGO && i != NAME_OF_MONTH_JP && i != JIKKAN_OF_YEAR && i != JUUNISHI_OF_YEAR && i != JIKKAN_OF_DAY
				&& i != JUUNISHI_OF_DAY && i != SHICHIYOU && i != DAY_OF_WEEK)
			throw new UnsupportedOperationException();

		// 和暦がセットされていなければ和暦作成処理
		if (!isWarekiSet)
			setFields();

		if (i == GENGO) {
			return GENGO_DATA[warekiFields[0]];
		} else if (i == NAME_OF_MONTH_JP) {
			if (warekiFields[5] < 21)
				return MONTH_NORMAL_DATA[warekiFields[5] - 1];
			else
				return MONTH_LEAP_DATA[warekiFields[5] - 21];
		} else if (i == JIKKAN_OF_YEAR) {
			return JIKKAN_DATA[warekiFields[6]];
		} else if (i == JUUNISHI_OF_YEAR) {
			return JUUNISHI_DATA[warekiFields[7]];
		} else if (i == JIKKAN_OF_DAY) {
			return JIKKAN_DATA[warekiFields[8]];
		} else if (i == JUUNISHI_OF_DAY) {
			return JUUNISHI_DATA[warekiFields[9]];
		} else if (i == SHICHIYOU) {
			return SHICHIYOU_DATA[get(Calendar.DAY_OF_WEEK) - 1];
		} else if (i == DAY_OF_WEEK) {
			int w = get(DAY_OF_WEEK);
			if (w == SUNDAY)
				return "日";
			if (w == MONDAY)
				return "月";
			if (w == TUESDAY)
				return "火";
			if (w == WEDNESDAY)
				return "水";
			if (w == THURSDAY)
				return "木";
			if (w == FRIDAY)
				return "金";
			if (w == SATURDAY)
				return "土";
		}
		return null;
	}

	/**
	 * 和暦をセットします。<br/>
	 * 月日は1月1日がセットされます<br/>
	 * 和暦の情報はsetメソッドでは登録できません。
	 *
	 * @param g 元号コード
	 * @param y 年
	 */
	public void setJapanese(int g, int y) {
		setJapanese(g, y, 1, 1);
	}

	/**
	 * 和暦をセットします。<br/>
	 * 和暦の情報はsetメソッドでは登録できません。<br/>
	 * 時間はリセットします。
	 *
	 * @param g 元号コード
	 * @param y 年（1～）
	 * @param m 月コード（1～12、21～32）21以降は閏月、該当月に＋20する。存在しない閏月を指定するとエラーとする。
	 * @param d 日（1～31）
	 */
	public void setJapanese(int g, int y, int m, int d) {
		if (g < 0 || g > REIWA)
			throw new IllegalArgumentException();

		if (m < 1 || m > 32 || (m > 12 && m < 21))
			throw new IllegalArgumentException();

		if (d < 1 || d > 31)
			throw new IllegalArgumentException();

		int start = GANNEN_DATA[g]; // 元年
		int year = start + y - 1; // 西暦

		if (year >= 1873) {
			// グレゴリオ暦
			if (m > 20)
				m -= 20;
			set(year, m - 1, d);
			isWarekiSet = false;
			return;
		}

		int[] data = YEAR_DATA[year - 593];
		if (m > 20) {
			// 指定した閏月が存在するか確認
			if (m - 20 != data[3])
				throw new IllegalArgumentException();
		}

		// mを1から始まるシーケンス値に変換
		if (data[3] > 0) {
			if (m > 20) {
				m = m - 20 + 1;
			} else if (m > data[3]) {
				m++;
			}
		}

		// 年内の指定日数を算出
		long days = 0;
		int[] monthData = getMonthData(data);
		for (int i = 0; i < data[2]; i++) {
			if (m - 1 == i) {
				days += d - 1;
				break;
			}
			if (monthData[i] == 1)
				days += 30;
			else
				days += 29;
		}

		long startTime = YEAR_START[year - 593];
		long add = days * 24 * 60 * 60 * 1000;
		setTimeInMillis(startTime + add);

		isWarekiSet = false;
	}

	/**
	 * 和暦を加算します<br/>
	 * YEAR_OF_GENGO,YEAR_OF_JP,MONTH_JP,DAY_OF_MONTH_JPが処理対象<br/>
	 * それ以外は、superクラスの挙動に依存<br/>
	 */
	public void add(int i, int j) {
		if (i == GENGO || i == JIKKAN_OF_YEAR || i == JUUNISHI_OF_YEAR || i == JIKKAN_OF_DAY || i == JUUNISHI_OF_DAY
				|| i == NAME_OF_MONTH_JP || i == SHICHIYOU)
			// 元号、十干十二支のaddは処理できない
			throw new IllegalArgumentException();

		if (j == 0)
			return;

		if (i == DAY_OF_MONTH_JP)
			// DAY_OF_MONTH_JPはDAY_OF_MONTHとして挙動
			i = DAY_OF_MONTH;
		if (i < 900 || i > 910) {
			// 本クラスで定義しているstaticfinal値以外は、superクラスの挙動に依存
			super.add(i, j);
			isWarekiSet = false;	// 取得時に設定されていなければ再算出
			return;
		}

		// 和暦がセットされていなければ和暦作成処理
		if (!isWarekiSet)
			setFields();

		if (i == YEAR_OF_GENGO || i == YEAR_OF_JP) {
			// 年の移動
			int y = warekiFields[2] + j;
			int m = warekiFields[5];
			int d = warekiFields[4];
			if (y < 593)
				throw new UnsupportedOperationException();
			if (y >= 1873) {
				// グレゴリオ変換
				m = m >= 21 ? m - 21 : m - 1;
				if (m != 2 || d < 29) {
					// mが2月じゃない、または28日以下
					set(y, m, d);
				} else {
					// mが2月で、29日以降
					set(y, m + 1, 1);
					super.add(DAY_OF_MONTH, -1);
				}
			} else {
				// 旧暦変換
				int[] data = YEAR_DATA[y - 593];
				if (m > 20) {
					// 指定した閏月が存在するか確認
					if (m - 20 != data[3])
						m -= 20;
				}
				if (y == 1872 && m == 12 && d > 2) {
					// 和暦最終月補正
					d = 2;
				}
				if (d == 30) {
					// 30日指定で、移動先月に30日がなければ29日に
					int[] monthData = getMonthData(data);
					if (monthData[getMonthIndex(y, m)] == 0) {
						d = 29;
					}
				}
				setJapanese(JapaneseCalendar.SUIKO, y - 593 + 1, m, d);
			}
			isWarekiSet = false;
			return;
		} else if (i == MONTH_JP) {
			// 月の移動
			int y = warekiFields[2];
			int m = warekiFields[3];
			int d = warekiFields[4];
			if (y >= 1873) {
				// 処理前がグレゴリオ
				int tmp = (y - 1873) * 12 + m + 1; // 1872年12月からの月数
				if (j > 0 || tmp > j * (-1)) {
					// 処理後もグレゴリオ
					super.add(MONTH, j);
					isWarekiSet = false;
					return;
				} else {
					// 処理後が和暦、処理前を1872年12月に変更
					y = 1872;
					m = 11;
					j += tmp;
				}
			}

			// 処理前が和暦
			int[] data = YEAR_DATA[y - 593];
			while (true) { // 処理後の該当年月に移動
				if (m + j >= 0 && data[2] > m + j) {
					// 同年内
					m = m + j;
					j = 0;
					break;
				}
				if (j > 0) {
					// 処理前を翌年1月に変更
					y++;
					j = j - (data[2] - m);
					m = 0;
					if (y > 1872) {
						break;
					}
					data = YEAR_DATA[y - 593];
				} else {
					// 処理前を前年1月に変更
					y--;
					if (y < 593) {
						throw new UnsupportedOperationException();
					}
					data = YEAR_DATA[y - 593];
					j = j + (data[2] + m);
					m = 0;
				}
			}

			if (y > 1872) {
				// 処理後がグレゴリオ
				super.setTimeInMillis(GREGORIAN_START);
				super.add(MONTH, j);
				isWarekiSet = false;
				return;
			}

			if (y == 1872 && m == 11 && d > 2) {
				// 和暦最終月補正
				d = 2;
			}
			if (d == 30) {
				// 30日指定で、処理後月に30日がなければ29日に
				int[] monthData = getMonthData(data);
				if (monthData[m] == 0) {
					d = 29;
				}
			}
			setJapanese(JapaneseCalendar.SUIKO, y - 593 + 1, getMonthName(y, m), d);
			isWarekiSet = false;
			return;
		}

	}

	public void complete() {
		isWarekiSet = false;
		super.complete();
	}

	/**
	 * 年号を北朝で取得するかを設定します。デフォルトはfalse（南朝）です。<br/>
	 * 南北朝時代（1331年～1392年）以外は本設定にかかわらず年号は同じ値が返ります。
	 *
	 * @param f
	 */
	public void setNorthImperialCourt(boolean f) {
		this.isNorthImperialCourt = f;
		isWarekiSet = false;
	}

	/**
	 * 年号を北朝で取得するかを取得します。
	 *
	 * @return
	 */
	public boolean isNorthImperialCourt() {
		return this.isNorthImperialCourt;
	}

	/**
	 * 指定した西暦年と同等の年の和暦における正月朔日0時0分0秒000のtimeInMillsを返す
	 *
	 * @param y 593年～1872年
	 * @return
	 */
	public static long getStartTimeInMillis(int y) {
		if (y < 593 || y > 1872)
			throw new UnsupportedOperationException();
		return YEAR_START[y - 593];
	}

	/**
	 * 指定した西暦年と同等の年の和暦にデータを返す
	 *
	 * @param y 593年～1872年
	 * @return 0:元号コード、1:元号コード（北朝：なければ-1）、2:月の数、3:閏月（1-12、なければ-1）、4:月並びコード
	 */
	public static int[] getYearData(int y) {
		if (y < 593 || y > 1872)
			throw new UnsupportedOperationException();
		return YEAR_DATA[y - 593];
	}

	/**
	 * 和暦月名称から月のインデックスを取得
	 *
	 * @param y 西暦年
	 * @param m 和暦の月の名称。正月は1、2月は2・・12月は12、閏正月は21、閏2月は22・・・閏12月は32
	 * @return 該当年の最初の月は0、最大12まで
	 * @throws IllegalArgumentException 存在しない月が指定された場合に返す
	 */
	private int getMonthIndex(int y, int m) {
		if (m < 1 || 32 < m || (12 < m && m < 21))
			throw new IllegalArgumentException();
		int ret = -1;
		if (y < 593)
			throw new UnsupportedOperationException();
		if (y > 1872) {
			if (m >= 21)
				throw new IllegalArgumentException();
			ret = m - 1;
			return ret;
		}

		int[] data = YEAR_DATA[y - 593];
		if (m >= 21) {
			// 指定した閏月が存在するか確認
			if (m - 20 != data[3])
				throw new IllegalArgumentException();
		}

		if (data[3] == -1) {
			ret = m - 1;
		} else if (m <= data[3]) {
			ret = m - 1;
		} else if (m - 20 == data[3]) {
			ret = m - 20;
		} else {
			ret = m;
		}
		return ret;
	}

	/**
	 * 和暦月インデックスから月名称を取得
	 *
	 * @param y 西暦年（当該日付の年ではなく、年としてのマッピング値）
	 * @param m 該当年の最初の月は0、最大12まで
	 * @return 和暦の月の名称。正月は1、2月は2・・12月は12、閏正月は21、閏2月は22・・・閏12月は32
	 * @throws IllegalArgumentException 存在しない月が指定された場合に返す
	 */
	private int getMonthName(int y, int m) {
		if (m < 0 || 12 < m)
			throw new IllegalArgumentException();
		int ret = -1;
		if (y < 593)
			throw new UnsupportedOperationException();
		if (y > 1872) {
			if (m == 12)
				throw new IllegalArgumentException();
			ret = m + 1;
			return ret;
		}

		int[] data = YEAR_DATA[y - 593];
		if (data[3] == -1) {
			if (m == 12)
				throw new IllegalArgumentException();
			ret = m + 1;
		} else if (m < data[3]) {
			ret = m + 1;
		} else if (m == data[3]) {
			ret = m + 20;
		} else {
			ret = m;
		}
		return ret;
	}

	/**
	 * 和暦生成処理
	 */
	private void setFields() {

		long timeInMills = getTimeInMillis();

		// ユリウス暦593年2月7日0時0分0秒000未満は、和暦データが存在しないため、エラーとする
		if (timeInMills < YEAR_START[0])
			throw new UnsupportedOperationException();

		// 西暦取得
		int year = super.get(Calendar.YEAR);

		// 元号
		if (timeInMills >= REIWA_START) {
			// 令和
			warekiFields[0] = REIWA;
		} else if (timeInMills >= HEISEI_START) {
			// 平成
			warekiFields[0] = HEISEI;
		} else if (timeInMills >= SHOUWA_START) {
			// 昭和
			warekiFields[0] = SHOUWA;
		} else if (timeInMills >= TAISHO_START) {
			// 大正
			warekiFields[0] = TAISHOU;
		} else if (timeInMills >= GREGORIAN_START) {
			// 明治（グレゴリオ暦）
			warekiFields[0] = MEIJI;
		} else {
			// 旧暦
			// 和暦で補正した暫定西暦年
			if (timeInMills < YEAR_START[year - 593]) {
				year--;
			}

			if (isNorthImperialCourt) {
				// 北朝
				int tmp = YEAR_DATA[year - 593][1];
				if (tmp == -1) {
					warekiFields[0] = YEAR_DATA[year - 593][0];
				} else {
					warekiFields[0] = tmp;
				}
			} else {
				// 南朝
				warekiFields[0] = YEAR_DATA[year - 593][0];
			}
		}

		// 元号年
		warekiFields[1] = year - GANNEN_DATA[warekiFields[0]] + 1;

		// 皇紀
		warekiFields[2] = year;

		if (timeInMills >= GREGORIAN_START) {
			// グレゴリアン
			warekiFields[3] = get(Calendar.MONTH);
			warekiFields[4] = get(Calendar.DAY_OF_MONTH);
			warekiFields[5] = warekiFields[3] + 1;
		} else {
			// 旧暦

			// 正月朔日からの日数を算出
			long start = YEAR_START[year - 593];
			int days = ((int) (timeInMills / 1000 - start / 1000)) / 86400;

			// 月、月内日数を算出
			int[] data = YEAR_DATA[year - 593];
			int cnt = 0;
			int[] monthData = getMonthData(data);
			for (int i = 0; i < data[2]; i++) {
				int dm = 29;
				if (monthData[i] == 1)
					dm = 30;
				if (days < cnt + dm) {
					warekiFields[3] = i;
					warekiFields[4] = days - cnt + 1;
					warekiFields[5] = warekiFields[3] + 1;
					break;
				}
				cnt += dm;
			}
			if (data[3] > 0) {
				if (warekiFields[5] == data[3] + 1)
					warekiFields[5] += 20 - 1;
				else if (warekiFields[5] > data[3] + 1)
					warekiFields[5]--;
			}
		}

		// 年十干
		warekiFields[6] = (year - 4) % 10;

		// 年十二支
		warekiFields[7] = (year - 4) % 12;

		// 紀元1年1月1日からの日数
		long c = (timeInMills / 1000 + 62167424400L) / 86400;
		// System.out.println(c);

		// 日十干
		warekiFields[8] = (int) (c - 3) % 10;

		// 日十二支
		warekiFields[9] = (int) (c - 5) % 12;

		isWarekiSet = true;
	}

	/**
	 * 年データから、月データの配列を返す
	 *
	 * @param yearData
	 * @return 該当年の月の大小を配列で返す。大の月＝１、小の月＝０
	 */
	private int[] getMonthData(int[] yearData) {
		int tmp = yearData[4];
		int cnt = yearData[2];
		int[] ret = new int[cnt];
		for (int i = 0; i < cnt; i++) {
			ret[i] = tmp % 2;
			tmp = tmp >>> 1;
		}
		return ret;
	}

	// グレゴリオ暦1873年（明治6年）1月1日0時0分0秒000のtimeInMills
	private static final long GREGORIAN_START = -3061011600000L;

	// グレゴリオ暦1912年7月30日0時0分0秒000のtimeInMills
	private static final long TAISHO_START = -1812186000000L;

	// グレゴリオ暦1926年12月25日0時0分0秒000のtimeInMills
	private static final long SHOUWA_START = -1357635600000L;

	// グレゴリオ暦1989年1月8日0時0分0秒000のtimeInMills
	private static final long HEISEI_START = 600188400000L;

	// グレゴリオ暦2019年5月1日0時0分0秒000のtimeInMills
	private static final long REIWA_START = 1556636400000L;

	// 月名
	private static final String[] MONTH_NORMAL_DATA = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" };
	private static final String[] MONTH_LEAP_DATA = { "閏1", "閏2", "閏3", "閏4", "閏5", "閏6", "閏7", "閏8", "閏9", "閏10",
			"閏11", "閏12" };
	// private static final String[] MONTH_NORMAL_DATA = { "正月", "二月", "三月",
	// "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月" };
	// private static final String[] MONTH_LEAP_DATA = { "閏正月", "閏二月", "閏三月",
	// "閏四月", "閏五月", "閏六月", "閏七月", "閏八月", "閏九月", "閏十月", "閏十一月", "閏十二月" };

	// 十干データ
	// index＝コード、value＝十干名
	private static final String[] JIKKAN_DATA = { "甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸" };

	// 十二支データ
	// index＝コード、value＝十二支名
	private static final String[] JUUNISHI_DATA = { "子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥" };

	// 七曜データ
	private static final String[] SHICHIYOU_DATA = { "日", "月", "火", "水", "木", "金", "土" };

	// 元号データ
	// index＝コード、value＝元号名
	private static final String[] GENGO_DATA = { "（推古）", "（舒明）", "（皇極）", "大化", "白雉", "（斉明）", "（天智）", "（天武）", "朱鳥",
			"（持統）", "（文武）", "大宝", "慶雲", "和銅", "霊亀", "養老", "神亀", "天平", "天平感宝", "天平勝宝", "天平宝字", "天平神護", "神護景雲", "宝亀",
			"天応", "延暦", "大同", "弘仁", "天長", "承和", "嘉祥", "仁寿", "斉衡", "天安", "貞観", "元慶", "仁和", "寛平", "昌泰", "延喜", "延長", "承平",
			"天慶", "天暦", "天徳", "応和", "康保", "安和", "天禄", "天延", "貞元", "天元", "永観", "寛和", "永延", "永祚", "正暦", "長徳", "長保", "寛弘",
			"長和", "寛仁", "治安", "万寿", "長元", "長暦", "長久", "寛徳", "永承", "天喜", "康平", "治暦", "延久", "承保", "承暦", "永保", "応徳", "寛治",
			"嘉保", "永長", "承徳", "康和", "長治", "嘉承", "天仁", "天永", "永久", "元永", "保安", "天治", "大治", "天承", "長承", "保延", "永治", "康治",
			"天養", "久安", "仁平", "久寿", "保元", "平治", "永暦", "応保", "長寛", "永万", "仁安", "嘉応", "承安", "安元", "治承", "養和", "寿永", "元暦",
			"文治", "建久", "正治", "建仁", "元久", "建永", "承元", "建暦", "建保", "承久", "貞応", "元仁", "嘉禄", "安貞", "寛喜", "貞永", "天福", "文暦",
			"嘉禎", "暦仁", "延応", "仁治", "寛元", "宝治", "建長", "康元", "正嘉", "正元", "文応", "弘長", "文永", "建治", "弘安", "正応", "永仁", "正安",
			"乾元", "嘉元", "徳治", "延慶", "応長", "正和", "文保", "元応", "元亨", "正中", "嘉暦", "元徳", "元弘", "建武", "延元", "興国", "正平", "建徳",
			"文中", "天授", "弘和", "元中", "正慶", "暦応", "康永", "貞和", "観応", "文和", "延文", "康安", "貞治", "応安", "永和", "康暦", "永徳", "至徳",
			"嘉慶", "康応", "明徳", "応永", "正長", "永享", "嘉吉", "文安", "宝徳", "享徳", "康正", "長禄", "寛正", "文正", "応仁", "文明", "長享", "延徳",
			"明応", "文亀", "永正", "大永", "享禄", "天文", "弘治", "永禄", "元亀", "天正", "文禄", "慶長", "元和", "寛永", "正保", "慶安", "承応", "明暦",
			"万治", "寛文", "延宝", "天和", "貞享", "元禄", "宝永", "正徳", "享保", "元文", "寛保", "延享", "寛延", "宝暦", "明和", "安永", "天明", "寛政",
			"享和", "文化", "文政", "天保", "弘化", "嘉永", "安政", "万延", "文久", "元治", "慶応", "明治", "大正", "昭和", "平成", "令和" };

	// 元年データ
	// index＝コード、value＝元年の西暦
	private static final int[] GANNEN_DATA = { 593, 629, 642, 645, 650, 655, 662, 672, 686, 687, 697, 701, 704, 708,
			715, 717, 724, 729, 749, 749, 757, 765, 767, 770, 781, 782, 806, 810, 824, 834, 848, 851, 854, 857, 859,
			877, 885, 889, 898, 901, 923, 931, 938, 947, 957, 961, 964, 968, 970, 973, 976, 978, 983, 985, 987, 989,
			990, 995, 999, 1004, 1012, 1017, 1021, 1024, 1028, 1037, 1040, 1044, 1046, 1053, 1058, 1065, 1069, 1074,
			1077, 1081, 1084, 1087, 1094, 1096, 1097, 1099, 1104, 1106, 1108, 1110, 1113, 1118, 1120, 1124, 1126, 1131,
			1132, 1135, 1141, 1142, 1144, 1145, 1151, 1154, 1156, 1159, 1160, 1161, 1163, 1165, 1166, 1169, 1171, 1175,
			1177, 1181, 1182, 1184, 1185, 1190, 1199, 1201, 1204, 1206, 1207, 1211, 1213, 1219, 1222, 1224, 1225, 1227,
			1229, 1232, 1233, 1234, 1235, 1238, 1239, 1240, 1243, 1247, 1249, 1256, 1257, 1259, 1260, 1261, 1264, 1275,
			1278, 1288, 1293, 1299, 1302, 1303, 1306, 1308, 1311, 1312, 1317, 1319, 1321, 1324, 1326, 1329, 1331, 1334,
			1336, 1340, 1346, 1370, 1372, 1375, 1381, 1384, 1332, 1338, 1342, 1345, 1350, 1352, 1356, 1361, 1362, 1368,
			1375, 1379, 1381, 1384, 1387, 1389, 1390, 1394, 1428, 1429, 1441, 1444, 1449, 1452, 1455, 1457, 1460, 1466,
			1467, 1469, 1487, 1489, 1492, 1501, 1504, 1521, 1528, 1532, 1555, 1558, 1570, 1573, 1592, 1596, 1615, 1624,
			1644, 1648, 1652, 1655, 1658, 1661, 1673, 1681, 1684, 1688, 1704, 1711, 1716, 1736, 1741, 1744, 1748, 1751,
			1764, 1772, 1781, 1789, 1801, 1804, 1818, 1830, 1844, 1848, 1854, 1860, 1861, 1864, 1865, 1868, 1912, 1926,
			1989, 2019 };

	// 元号コード
	static final int SUIKO = 0;
	static final int JOMEI = 1;
	static final int KOUGYOKU = 2;
	static final int TAIKA = 3;
	static final int HAKUCHI = 4;
	static final int SAIMEI = 5;
	static final int TENJI_1 = 6;
	static final int TENMU = 7;
	static final int SHUCHOU = 8;
	static final int JITOU = 9;
	static final int MONMU = 10;
	static final int TAIHOU = 11;
	static final int KEIUN = 12;
	static final int WADOU = 13;
	static final int REIKI = 14;
	static final int YOUROU = 15;
	static final int JINKI = 16;
	static final int TENPYOU = 17;
	static final int TENPYOUKANPOU = 18;
	static final int TENPYOUSHOUHOU = 19;
	static final int TENPYOUHOUJI = 20;
	static final int TENPYOUJINGO = 21;
	static final int JINGOKEIUN = 22;
	static final int HOUKI = 23;
	static final int TENOU = 24;
	static final int ENRYAKU = 25;
	static final int DAIDOU = 26;
	static final int KOUNIN = 27;
	static final int TENCHOU = 28;
	static final int JOUWA_1 = 29;
	static final int KASHOU_1 = 30;
	static final int NINJU = 31;
	static final int SAIKOU = 32;
	static final int TENNAN = 33;
	static final int JOUGAN = 34;
	static final int GANGYOU = 35;
	static final int NINNA = 36;
	static final int KANPYOU = 37;
	static final int SHOUTAI = 38;
	static final int ENGI = 39;
	static final int ENCHOU = 40;
	static final int JOUHEI = 41;
	static final int TENGYOU = 42;
	static final int TANRYAKU = 43;
	static final int TENTOKU = 44;
	static final int OUWA = 45;
	static final int KOUHOU = 46;
	static final int ANNA = 47;
	static final int TENROKU = 48;
	static final int TENEN = 49;
	static final int JOUGEN_1 = 50;
	static final int TENGEN = 51;
	static final int EIKAN = 52;
	static final int KANNA = 53;
	static final int EIEN = 54;
	static final int EISO = 55;
	static final int SHOURYAKU = 56;
	static final int CHOUTOKU = 57;
	static final int CHOUHOU = 58;
	static final int KANKOU = 59;
	static final int CHOUWA = 60;
	static final int KANNIN = 61;
	static final int JIAN = 62;
	static final int MANJU = 63;
	static final int CHOUGEN = 64;
	static final int CHOURYAKU = 65;
	static final int CHOUKYUU = 66;
	static final int KANTOKU = 67;
	static final int EISHOU_1 = 68;
	static final int TENGI = 69;
	static final int KOUHEI = 70;
	static final int JIRYAKU = 71;
	static final int ENKYUU = 72;
	static final int JOUHOU = 73;
	static final int JOURYAKU = 74;
	static final int EIHOU = 75;
	static final int OUTOKU = 76;
	static final int KANJI = 77;
	static final int KAHOU = 78;
	static final int EICHOU = 79;
	static final int JOUTOKU = 80;
	static final int KOUWA_1 = 81;
	static final int CHOUJI = 82;
	static final int KASHOU_2 = 83;
	static final int TENNIN = 84;
	static final int TENEI = 85;
	static final int EIKYUU = 86;
	static final int GENNEI = 87;
	static final int HOUAN = 88;
	static final int TENJI_2 = 89;
	static final int DAIJI = 90;
	static final int TENSHOU_1 = 91;
	static final int CHOUSHOU = 92;
	static final int HOUEN = 93;
	static final int EIJI = 94;
	static final int KOUJI_1 = 95;
	static final int ENYOU = 96;
	static final int KYUUAN = 97;
	static final int NINPEI = 98;
	static final int KYUUJU = 99;
	static final int HOUGEN = 100;
	static final int HEIJI = 101;
	static final int EIRYAKU = 102;
	static final int OUHOU = 103;
	static final int CHOUKAN = 104;
	static final int EIMAN = 105;
	static final int NINAN = 106;
	static final int KAOU = 107;
	static final int JOUAN = 108;
	static final int ANGEN = 109;
	static final int JISHOU = 110;
	static final int YOUWA = 111;
	static final int JUEI = 112;
	static final int GENRYAKU = 113;
	static final int BUNJI = 114;
	static final int KENKYUU = 115;
	static final int SHOUJI = 116;
	static final int KENNIN = 117;
	static final int GENKYUU = 118;
	static final int GENEI = 119;
	static final int JOUGEN_2 = 120;
	static final int KENRYAKU = 121;
	static final int KENPOU = 122;
	static final int JOUKYUU = 123;
	static final int JOUOU_1 = 124;
	static final int GENNIN = 125;
	static final int KAROKU = 126;
	static final int ANTEI = 127;
	static final int KANKI = 128;
	static final int JOUEI = 129;
	static final int TENPUKU = 130;
	static final int BUNRYAKU = 131;
	static final int KATEI = 132;
	static final int RYAKUNIN = 133;
	static final int ENOU = 134;
	static final int NINJI = 135;
	static final int KANGEN = 136;
	static final int HOUJI = 137;
	static final int KENCHOU = 138;
	static final int KOUGEN = 139;
	static final int SHOUKA = 140;
	static final int SHOUGEN = 141;
	static final int BUNOU = 142;
	static final int KOUCHOU = 143;
	static final int BUNEI = 144;
	static final int KENJI = 145;
	static final int KOUAN_1 = 146;
	static final int SHOUOU = 147;
	static final int EININ = 148;
	static final int SHOUAN = 149;
	static final int KENGEN = 150;
	static final int KAGEN = 151;
	static final int TOKUJI = 152;
	static final int ENGYOU = 153;
	static final int OUCHOU = 154;
	static final int SHOUWA_1 = 155;
	static final int BUNPOU = 156;
	static final int GENOU = 157;
	static final int GENKOU_1 = 158;
	static final int SHOUCHUU = 159;
	static final int KARYAKU = 160;
	static final int GENTOKU = 161;
	static final int GENKOU_2 = 162;
	static final int KENMU = 163;
	static final int ENGEN = 164;
	static final int KOUKOKU = 165;
	static final int SHOUHEI = 166;
	static final int KENTOKU = 167;
	static final int BUNCHUU = 168;
	static final int TENJU = 169;
	static final int KOUWA_2 = 170;
	static final int GENCHUU = 171;
	static final int SHOUKYOU = 172;
	static final int RYAKUOU = 173;
	static final int KOUEI = 174;
	static final int JOUWA_2 = 175;
	static final int KANOU = 176;
	static final int BUNNA = 177;
	static final int ENBUN = 178;
	static final int KOUAN_2 = 179;
	static final int JOUJI = 180;
	static final int OUAN = 181;
	static final int EIWA = 182;
	static final int KOURYAKU = 183;
	static final int EITOKU = 184;
	static final int SITOKU = 185;
	static final int KAKEI = 186;
	static final int KOUOU = 187;
	static final int MEITOKU = 188;
	static final int OUEI = 189;
	static final int SHOUCHOU = 190;
	static final int EIKYOU = 191;
	static final int KAKITSU = 192;
	static final int BUNAN = 193;
	static final int HOUTOKU = 194;
	static final int KYOUTOKU = 195;
	static final int KOUSHOU = 196;
	static final int CHOUROKU = 197;
	static final int KANSHOU = 198;
	static final int BUNSHOU = 199;
	static final int OUNIN = 200;
	static final int BUNMEI = 201;
	static final int CHOUKYOU = 202;
	static final int ENTOKU = 203;
	static final int MEIOU = 204;
	static final int BUNKI = 205;
	static final int EISHOU_2 = 206;
	static final int DAIEI = 207;
	static final int KYOUROKU = 208;
	static final int TENBUN = 209;
	static final int KOUJI_2 = 210;
	static final int EIROKU = 211;
	static final int GENKI = 212;
	static final int TENSHOU_2 = 213;
	static final int BUNROKU = 214;
	static final int KEICHOU = 215;
	static final int GENNA = 216;
	static final int KANEI = 217;
	static final int SHOUHOU = 218;
	static final int KEIAN = 219;
	static final int JOUOU_2 = 220;
	static final int MEIREKI = 221;
	static final int MANJI = 222;
	static final int KANBUN = 223;
	static final int EIPOU = 224;
	static final int TENNA = 225;
	static final int JOUKYOU = 226;
	static final int GENROKU = 227;
	static final int HOUEI = 228;
	static final int SHOUTOKU = 229;
	static final int KYOUHOU = 230;
	static final int GENBUN = 231;
	static final int KANPOU = 232;
	static final int ENKYOU = 233;
	static final int KANEN = 234;
	static final int HOUREKI = 235;
	static final int MEIWA = 236;
	static final int ANEI = 237;
	static final int TENMEI = 238;
	static final int KANSEI = 239;
	static final int KYOUWA = 240;
	static final int BUNKA = 241;
	static final int BUNSEI = 242;
	static final int TENPOU = 243;
	static final int KOUKA = 244;
	static final int KAEI = 245;
	static final int ANSEI = 246;
	static final int MANEN = 247;
	static final int BUNKYUU = 248;
	static final int GENJI = 249;
	static final int KEIOU = 250;
	static final int MEIJI = 251;
	static final int TAISHOU = 252;
	static final int SHOUWA = 253;
	static final int HEISEI = 254;
	static final int REIWA = 255;

	// 和暦における各年正月朔日0時0分0秒000のtimeInMills
	// index＝593年～1872年の西暦（指定年-593）、value=timeInMills
	private static final long[] YEAR_START = { -43450506000000L, -43419920400000L, -43386742800000L, -43356157200000L,
			-43325485200000L, -43292307600000L, -43261722000000L, -43231136400000L, -43197958800000L, -43167286800000L,
			-43134195600000L, -43103523600000L, -43072938000000L, -43039760400000L, -43009174800000L, -42978502800000L,
			-42945325200000L, -42914739600000L, -42881562000000L, -42850976400000L, -42820304400000L, -42787126800000L,
			-42756541200000L, -42725955600000L, -42692778000000L, -42662106000000L, -42631520400000L, -42598342800000L,
			-42567757200000L, -42534579600000L, -42503994000000L, -42473322000000L, -42440144400000L, -42409558800000L,
			-42378973200000L, -42345795600000L, -42315123600000L, -42281946000000L, -42251360400000L, -42220774800000L,
			-42187597200000L, -42156925200000L, -42126339600000L, -42093162000000L, -42062576400000L, -42031904400000L,
			-41998726800000L, -41968141200000L, -41934963600000L, -41904378000000L, -41873706000000L, -41840614800000L,
			-41809942800000L, -41779357200000L, -41746179600000L, -41715594000000L, -41682416400000L, -41651744400000L,
			-41621158800000L, -41587981200000L, -41557395600000L, -41526723600000L, -41493546000000L, -41462960400000L,
			-41432374800000L, -41399197200000L, -41368525200000L, -41335434000000L, -41304762000000L, -41274176400000L,
			-41240998800000L, -41210413200000L, -41179741200000L, -41146563600000L, -41115978000000L, -41082800400000L,
			-41052214800000L, -41021542800000L, -40988365200000L, -40957779600000L, -40927194000000L, -40894016400000L,
			-40863344400000L, -40832758800000L, -40799581200000L, -40768995600000L, -40735818000000L, -40705146000000L,
			-40674560400000L, -40641382800000L, -40610797200000L, -40580125200000L, -40547034000000L, -40516362000000L,
			-40483184400000L, -40452598800000L, -40422013200000L, -40388835600000L, -40358163600000L, -40327578000000L,
			-40294400400000L, -40263814800000L, -40233142800000L, -40199965200000L, -40169379600000L, -40136202000000L,
			-40105530000000L, -40074944400000L, -40041766800000L, -40011181200000L, -39980595600000L, -39947418000000L,
			-39916746000000L, -39886160400000L, -39852982800000L, -39822310800000L, -39789219600000L, -39758634000000L,
			-39728048400000L, -39694870800000L, -39664198800000L, -39633526800000L, -39600349200000L, -39569763600000L,
			-39536586000000L, -39506000400000L, -39475414800000L, -39442237200000L, -39411651600000L, -39380979600000L,
			-39347802000000L, -39317130000000L, -39286544400000L, -39253366800000L, -39222781200000L, -39189603600000L,
			-39159018000000L, -39128432400000L, -39095254800000L, -39064582800000L, -39033910800000L, -39000819600000L,
			-38970147600000L, -38937056400000L, -38906470800000L, -38875798800000L, -38842621200000L, -38812035600000L,
			-38781363600000L, -38748186000000L, -38717600400000L, -38686928400000L, -38653837200000L, -38623251600000L,
			-38590074000000L, -38559402000000L, -38528816400000L, -38495638800000L, -38464966800000L, -38434381200000L,
			-38401203600000L, -38370618000000L, -38337440400000L, -38306854800000L, -38276182800000L, -38243005200000L,
			-38212419600000L, -38181747600000L, -38148570000000L, -38118070800000L, -38084893200000L, -38054221200000L,
			-38023635600000L, -37990458000000L, -37959786000000L, -37929200400000L, -37896022800000L, -37865350800000L,
			-37834765200000L, -37801674000000L, -37771002000000L, -37737824400000L, -37707238800000L, -37676566800000L,
			-37643389200000L, -37612803600000L, -37582218000000L, -37548954000000L, -37518368400000L, -37487869200000L,
			-37454691600000L, -37424019600000L, -37390842000000L, -37360170000000L, -37329584400000L, -37296406800000L,
			-37265821200000L, -37235235600000L, -37202058000000L, -37171472400000L, -37138294800000L, -37107622800000L,
			-37076950800000L, -37043773200000L, -37013187600000L, -36982602000000L, -36949424400000L, -36918838800000L,
			-36888253200000L, -36855075600000L, -36824403600000L, -36791226000000L, -36760640400000L, -36729968400000L,
			-36696790800000L, -36666291600000L, -36635619600000L, -36602442000000L, -36571856400000L, -36538678800000L,
			-36508006800000L, -36477421200000L, -36444243600000L, -36413658000000L, -36383072400000L, -36349894800000L,
			-36319222800000L, -36288637200000L, -36255459600000L, -36224787600000L, -36191610000000L, -36161024400000L,
			-36130438800000L, -36097347600000L, -36066675600000L, -36036003600000L, -36002826000000L, -35972240400000L,
			-35939062800000L, -35908390800000L, -35877805200000L, -35844714000000L, -35814042000000L, -35783456400000L,
			-35750278800000L, -35719606800000L, -35689021200000L, -35655843600000L, -35625258000000L, -35592080400000L,
			-35561494800000L, -35530909200000L, -35497731600000L, -35467059600000L, -35436387600000L, -35403210000000L,
			-35372624400000L, -35339446800000L, -35308861200000L, -35278275600000L, -35245098000000L, -35214512400000L,
			-35183754000000L, -35150662800000L, -35119990800000L, -35089405200000L, -35056227600000L, -35025642000000L,
			-34992464400000L, -34961878800000L, -34931293200000L, -34898115600000L, -34867443600000L, -34836771600000L,
			-34803594000000L, -34773008400000L, -34739917200000L, -34709245200000L, -34678659600000L, -34645482000000L,
			-34614810000000L, -34584138000000L, -34551046800000L, -34520461200000L, -34489875600000L, -34456698000000L,
			-34426112400000L, -34392934800000L, -34362262800000L, -34331677200000L, -34298499600000L, -34267827600000L,
			-34237242000000L, -34204064400000L, -34173478800000L, -34140301200000L, -34109715600000L, -34079043600000L,
			-34045952400000L, -34015194000000L, -33984608400000L, -33951430800000L, -33920845200000L, -33890259600000L,
			-33857082000000L, -33826496400000L, -33793318800000L, -33762646800000L, -33732061200000L, -33698883600000L,
			-33668211600000L, -33637626000000L, -33604534800000L, -33573862800000L, -33540771600000L, -33510099600000L,
			-33479427600000L, -33446250000000L, -33415664400000L, -33385078800000L, -33351901200000L, -33321315600000L,
			-33290730000000L, -33257552400000L, -33226880400000L, -33193702800000L, -33163030800000L, -33132445200000L,
			-33099267600000L, -33068682000000L, -33038096400000L, -33004918800000L, -32974333200000L, -32941155600000L,
			-32910483600000L, -32879811600000L, -32846634000000L, -32816048400000L, -32785462800000L, -32752371600000L,
			-32721699600000L, -32691114000000L, -32657936400000L, -32627264400000L, -32594173200000L, -32563587600000L,
			-32532915600000L, -32499738000000L, -32469152400000L, -32438480400000L, -32405302800000L, -32374717200000L,
			-32341539600000L, -32310867600000L, -32280282000000L, -32247104400000L, -32216518800000L, -32185933200000L,
			-32152755600000L, -32122083600000L, -32091498000000L, -32058320400000L, -32027648400000L, -31994470800000L,
			-31963885200000L, -31933299600000L, -31900122000000L, -31869536400000L, -31838864400000L, -31805686800000L,
			-31775101200000L, -31741923600000L, -31711338000000L, -31680666000000L, -31647574800000L, -31616902800000L,
			-31586317200000L, -31553139600000L, -31522467600000L, -31491882000000L, -31458704400000L, -31428118800000L,
			-31394941200000L, -31364355600000L, -31333770000000L, -31300592400000L, -31269920400000L, -31239248400000L,
			-31206070800000L, -31175485200000L, -31142307600000L, -31111722000000L, -31081136400000L, -31047958800000L,
			-31017373200000L, -30986701200000L, -30953523600000L, -30922851600000L, -30892266000000L, -30859088400000L,
			-30828502800000L, -30795411600000L, -30764739600000L, -30734154000000L, -30700976400000L, -30670304400000L,
			-30639718800000L, -30606541200000L, -30575955600000L, -30542778000000L, -30512192400000L, -30481520400000L,
			-30448342800000L, -30417757200000L, -30387085200000L, -30353907600000L, -30323322000000L, -30292736400000L,
			-30259558800000L, -30228973200000L, -30195795600000L, -30165123600000L, -30134538000000L, -30101360400000L,
			-30070688400000L, -30040102800000L, -30006925200000L, -29976339600000L, -29943162000000L, -29912576400000L,
			-29881904400000L, -29848726800000L, -29818141200000L, -29787469200000L, -29754291600000L, -29723706000000L,
			-29693120400000L, -29659942800000L, -29629357200000L, -29596179600000L, -29565507600000L, -29534922000000L,
			-29501744400000L, -29471158800000L, -29440573200000L, -29407395600000L, -29376810000000L, -29343632400000L,
			-29312960400000L, -29282288400000L, -29249110800000L, -29218525200000L, -29187939600000L, -29154762000000L,
			-29124176400000L, -29093590800000L, -29060413200000L, -29029741200000L, -28996563600000L, -28965891600000L,
			-28935306000000L, -28902128400000L, -28871542800000L, -28840957200000L, -28807779600000L, -28777194000000L,
			-28744016400000L, -28713344400000L, -28682758800000L, -28649581200000L, -28618995600000L, -28588410000000L,
			-28555232400000L, -28524560400000L, -28493974800000L, -28460797200000L, -28430125200000L, -28396947600000L,
			-28366362000000L, -28335776400000L, -28302598800000L, -28272013200000L, -28241341200000L, -28208163600000L,
			-28177578000000L, -28144400400000L, -28113728400000L, -28083142800000L, -28049965200000L, -28019379600000L,
			-27988794000000L, -27955616400000L, -27924944400000L, -27894358800000L, -27861181200000L, -27830509200000L,
			-27797418000000L, -27766746000000L, -27736160400000L, -27703069200000L, -27672397200000L, -27641811600000L,
			-27608634000000L, -27577962000000L, -27547376400000L, -27514198800000L, -27483613200000L, -27450435600000L,
			-27419850000000L, -27389178000000L, -27356000400000L, -27325328400000L, -27294742800000L, -27261565200000L,
			-27230979600000L, -27197802000000L, -27167216400000L, -27136630800000L, -27103453200000L, -27072781200000L,
			-27042109200000L, -27008931600000L, -26978346000000L, -26947760400000L, -26914582800000L, -26883997200000L,
			-26850819600000L, -26820234000000L, -26789562000000L, -26756384400000L, -26725798800000L, -26695126800000L,
			-26662035600000L, -26631450000000L, -26598272400000L, -26567600400000L, -26537014800000L, -26503837200000L,
			-26473165200000L, -26442579600000L, -26409402000000L, -26378816400000L, -26345638800000L, -26315053200000L,
			-26284467600000L, -26251290000000L, -26220618000000L, -26189946000000L, -26156768400000L, -26126182800000L,
			-26095597200000L, -26062419600000L, -26031834000000L, -25998656400000L, -25968070800000L, -25937398800000L,
			-25904221200000L, -25873549200000L, -25842963600000L, -25809786000000L, -25779200400000L, -25746109200000L,
			-25715437200000L, -25684851600000L, -25651674000000L, -25621002000000L, -25590416400000L, -25557238800000L,
			-25526653200000L, -25496067600000L, -25462890000000L, -25432218000000L, -25399040400000L, -25368368400000L,
			-25337782800000L, -25304605200000L, -25274019600000L, -25243434000000L, -25210256400000L, -25179670800000L,
			-25146493200000L, -25115821200000L, -25085235600000L, -25052058000000L, -25021386000000L, -24990800400000L,
			-24957622800000L, -24927037200000L, -24896451600000L, -24863274000000L, -24832602000000L, -24799424400000L,
			-24768838800000L, -24738166800000L, -24705075600000L, -24674490000000L, -24643818000000L, -24610640400000L,
			-24580054800000L, -24546877200000L, -24516205200000L, -24485619600000L, -24452442000000L, -24421856400000L,
			-24391270800000L, -24358093200000L, -24327507600000L, -24296835600000L, -24263658000000L, -24232986000000L,
			-24199808400000L, -24169222800000L, -24138637200000L, -24105459600000L, -24074874000000L, -24044288400000L,
			-24011110800000L, -23980438800000L, -23949766800000L, -23916589200000L, -23886003600000L, -23852826000000L,
			-23822240400000L, -23791654800000L, -23758477200000L, -23727891600000L, -23697219600000L, -23664042000000L,
			-23633456400000L, -23600278800000L, -23569693200000L, -23539107600000L, -23505930000000L, -23475258000000L,
			-23444672400000L, -23411494800000L, -23380822800000L, -23350237200000L, -23317059600000L, -23286474000000L,
			-23253296400000L, -23222710800000L, -23192038800000L, -23158861200000L, -23128275600000L, -23097603600000L,
			-23064426000000L, -23033840400000L, -23000662800000L, -22970077200000L, -22939491600000L, -22906314000000L,
			-22875642000000L, -22845056400000L, -22811878800000L, -22781206800000L, -22748115600000L, -22717530000000L,
			-22686858000000L, -22653680400000L, -22623094800000L, -22592422800000L, -22559245200000L, -22528659600000L,
			-22498074000000L, -22464896400000L, -22434310800000L, -22401133200000L, -22370547600000L, -22339875600000L,
			-22306698000000L, -22276026000000L, -22245440400000L, -22212262800000L, -22181677200000L, -22151091600000L,
			-22117914000000L, -22087328400000L, -22054150800000L, -22023478800000L, -21992806800000L, -21959629200000L,
			-21929043600000L, -21898458000000L, -21865280400000L, -21834694800000L, -21801517200000L, -21770931600000L,
			-21740259600000L, -21707082000000L, -21676496400000L, -21645824400000L, -21612733200000L, -21582147600000L,
			-21548970000000L, -21518298000000L, -21487712400000L, -21454534800000L, -21423862800000L, -21393277200000L,
			-21360099600000L, -21329514000000L, -21298928400000L, -21265750800000L, -21235078800000L, -21201901200000L,
			-21171315600000L, -21140643600000L, -21107466000000L, -21076880400000L, -21046294800000L, -21013117200000L,
			-20982531600000L, -20949354000000L, -20918682000000L, -20888096400000L, -20854918800000L, -20824246800000L,
			-20793661200000L, -20760570000000L, -20729898000000L, -20699312400000L, -20666134800000L, -20635462800000L,
			-20602371600000L, -20571699600000L, -20541114000000L, -20507936400000L, -20477350800000L, -20446765200000L,
			-20413587600000L, -20382915600000L, -20352243600000L, -20319066000000L, -20288480400000L, -20255302800000L,
			-20224717200000L, -20194131600000L, -20160954000000L, -20130368400000L, -20099696400000L, -20066518800000L,
			-20035846800000L, -20002755600000L, -19972083600000L, -19941498000000L, -19908320400000L, -19877734800000L,
			-19847149200000L, -19813971600000L, -19783299600000L, -19752714000000L, -19719536400000L, -19688864400000L,
			-19655773200000L, -19625187600000L, -19594515600000L, -19561338000000L, -19530752400000L, -19500080400000L,
			-19466902800000L, -19436317200000L, -19403139600000L, -19372554000000L, -19341968400000L, -19308790800000L,
			-19278118800000L, -19247533200000L, -19214355600000L, -19183683600000L, -19153098000000L, -19119920400000L,
			-19089334800000L, -19056157200000L, -19025571600000L, -18994899600000L, -18961722000000L, -18931136400000L,
			-18900464400000L, -18867286800000L, -18836701200000L, -18803610000000L, -18772938000000L, -18742352400000L,
			-18709174800000L, -18678502800000L, -18647917200000L, -18614739600000L, -18584154000000L, -18553568400000L,
			-18520390800000L, -18489805200000L, -18456627600000L, -18425955600000L, -18395283600000L, -18362106000000L,
			-18331520400000L, -18300934800000L, -18267757200000L, -18237171600000L, -18203994000000L, -18173408400000L,
			-18142736400000L, -18109558800000L, -18078886800000L, -18048301200000L, -18015123600000L, -17984538000000L,
			-17953952400000L, -17920774800000L, -17890189200000L, -17857011600000L, -17826339600000L, -17795754000000L,
			-17762576400000L, -17731904400000L, -17701318800000L, -17668227600000L, -17637555600000L, -17604378000000L,
			-17573792400000L, -17543120400000L, -17509942800000L, -17479357200000L, -17448771600000L, -17415594000000L,
			-17385008400000L, -17354336400000L, -17321158800000L, -17290573200000L, -17257395600000L, -17226723600000L,
			-17196138000000L, -17162960400000L, -17132374800000L, -17101789200000L, -17068611600000L, -17037939600000L,
			-17004762000000L, -16974176400000L, -16943504400000L, -16910413200000L, -16879741200000L, -16849155600000L,
			-16815978000000L, -16785392400000L, -16754720400000L, -16721542800000L, -16690957200000L, -16657779600000L,
			-16627194000000L, -16596608400000L, -16563430800000L, -16532845200000L, -16502173200000L, -16468995600000L,
			-16438323600000L, -16405232400000L, -16374560400000L, -16343974800000L, -16310797200000L, -16280211600000L,
			-16249626000000L, -16216448400000L, -16185776400000L, -16155104400000L, -16121926800000L, -16091341200000L,
			-16058163600000L, -16027578000000L, -15996992400000L, -15963814800000L, -15933229200000L, -15902557200000L,
			-15869379600000L, -15838794000000L, -15805616400000L, -15774944400000L, -15744445200000L, -15711267600000L,
			-15680595600000L, -15650010000000L, -15616832400000L, -15586160400000L, -15555574800000L, -15522397200000L,
			-15491811600000L, -15458634000000L, -15428048400000L, -15397376400000L, -15364198800000L, -15333613200000L,
			-15302941200000L, -15269763600000L, -15239178000000L, -15206000400000L, -15175414800000L, -15144829200000L,
			-15111651600000L, -15080979600000L, -15050394000000L, -15017216400000L, -14986544400000L, -14955958800000L,
			-14922781200000L, -14892195600000L, -14859104400000L, -14828432400000L, -14797846800000L, -14764582800000L,
			-14733997200000L, -14703325200000L, -14670234000000L, -14639648400000L, -14606470800000L, -14575885200000L,
			-14545213200000L, -14512035600000L, -14481363600000L, -14450778000000L, -14417600400000L, -14387014800000L,
			-14356429200000L, -14323251600000L, -14292666000000L, -14259488400000L, -14228816400000L, -14198144400000L,
			-14164966800000L, -14134381200000L, -14103795600000L, -14070618000000L, -14040032400000L, -14006854800000L,
			-13976269200000L, -13945597200000L, -13912419600000L, -13881834000000L, -13851162000000L, -13818070800000L,
			-13787485200000L, -13756813200000L, -13723635600000L, -13693050000000L, -13659872400000L, -13629200400000L,
			-13598614800000L, -13565437200000L, -13534851600000L, -13504266000000L, -13471088400000L, -13440502800000L,
			-13407238800000L, -13376653200000L, -13345981200000L, -13312803600000L, -13282218000000L, -13251632400000L,
			-13218454800000L, -13187869200000L, -13157283600000L, -13124106000000L, -13093434000000L, -13060256400000L,
			-13029584400000L, -12998998800000L, -12965821200000L, -12935235600000L, -12904650000000L, -12871472400000L,
			-12840886800000L, -12807622800000L, -12777037200000L, -12746365200000L, -12713274000000L, -12682688400000L,
			-12652102800000L, -12618925200000L, -12588253200000L, -12557667600000L, -12524403600000L, -12493818000000L,
			-12460640400000L, -12430054800000L, -12399469200000L, -12366291600000L, -12335706000000L, -12305034000000L,
			-12271856400000L, -12241270800000L, -12210598800000L, -12177421200000L, -12146835600000L, -12113658000000L,
			-12083072400000L, -12052486800000L, -12019309200000L, -11988637200000L, -11958051600000L, -11924874000000L,
			-11894202000000L, -11861110800000L, -11830525200000L, -11799853200000L, -11766675600000L, -11736090000000L,
			-11705418000000L, -11672240400000L, -11641654800000L, -11608477200000L, -11577891600000L, -11547306000000L,
			-11514128400000L, -11483542800000L, -11452870800000L, -11419693200000L, -11389021200000L, -11358435600000L,
			-11325258000000L, -11294672400000L, -11261494800000L, -11230909200000L, -11200323600000L, -11167146000000L,
			-11136474000000L, -11105802000000L, -11072624400000L, -11042038800000L, -11008861200000L, -10978275600000L,
			-10947690000000L, -10914512400000L, -10883926800000L, -10853254800000L, -10820077200000L, -10789491600000L,
			-10758819600000L, -10725728400000L, -10695142800000L, -10661965200000L, -10631293200000L, -10600707600000L,
			-10567443600000L, -10536858000000L, -10506272400000L, -10473094800000L, -10442509200000L, -10409331600000L,
			-10378746000000L, -10348074000000L, -10314896400000L, -10284310800000L, -10253638800000L, -10220461200000L,
			-10189875600000L, -10159290000000L, -10126112400000L, -10095526800000L, -10062349200000L, -10031677200000L,
			-10001091600000L, -9967914000000L, -9937242000000L, -9906656400000L, -9873565200000L, -9842893200000L,
			-9809715600000L, -9779130000000L, -9748458000000L, -9715280400000L, -9684694800000L, -9654109200000L,
			-9620931600000L, -9590346000000L, -9559760400000L, -9526582800000L, -9495910800000L, -9462733200000L,
			-9432061200000L, -9401475600000L, -9368298000000L, -9337712400000L, -9307126800000L, -9273949200000L,
			-9243363600000L, -9210186000000L, -9179514000000L, -9148842000000L, -9115664400000L, -9085078800000L,
			-9054493200000L, -9021315600000L, -8990730000000L, -8960144400000L, -8926966800000L, -8896294800000L,
			-8865709200000L, -8832531600000L, -8801946000000L, -8768768400000L, -8738182800000L, -8707597200000L,
			-8674419600000L, -8643747600000L, -8613075600000L, -8579898000000L, -8549312400000L, -8516134800000L,
			-8485549200000L, -8454963600000L, -8421786000000L, -8391200400000L, -8360528400000L, -8327350800000L,
			-8296678800000L, -8266093200000L, -8232915600000L, -8202330000000L, -8169238800000L, -8138566800000L,
			-8107981200000L, -8074717200000L, -8044131600000L, -8013459600000L, -7980368400000L, -7949782800000L,
			-7916605200000L, -7886019600000L, -7855347600000L, -7822170000000L, -7791584400000L, -7760912400000L,
			-7727734800000L, -7697149200000L, -7666563600000L, -7633386000000L, -7602800400000L, -7569622800000L,
			-7538950800000L, -7508365200000L, -7475187600000L, -7444515600000L, -7413930000000L, -7380752400000L,
			-7350166800000L, -7316989200000L, -7286403600000L, -7255731600000L, -7222554000000L, -7191968400000L,
			-7161296400000L, -7128118800000L, -7097619600000L, -7064442000000L, -7033770000000L, -7003184400000L,
			-6970006800000L, -6939334800000L, -6908749200000L, -6875571600000L, -6844986000000L, -6814400400000L,
			-6781222800000L, -6750637200000L, -6717459600000L, -6686787600000L, -6656115600000L, -6622938000000L,
			-6592352400000L, -6561766800000L, -6528589200000L, -6498003600000L, -6464826000000L, -6434240400000L,
			-6403568400000L, -6370390800000L, -6339718800000L, -6309133200000L, -6275955600000L, -6245370000000L,
			-6214784400000L, -6181606800000L, -6151021200000L, -6117843600000L, -6087171600000L, -6056586000000L,
			-6023408400000L, -5992822800000L, -5962237200000L, -5929059600000L, -5898387600000L, -5867802000000L,
			-5834624400000L, -5803952400000L, -5770774800000L, -5740189200000L, -5709603600000L, -5676426000000L,
			-5645840400000L, -5615168400000L, -5581990800000L, -5551405200000L, -5518227600000L, -5487555600000L,
			-5456970000000L, -5423792400000L, -5393206800000L, -5362621200000L, -5329443600000L, -5298771600000L,
			-5268186000000L, -5235008400000L, -5204336400000L, -5171245200000L, -5140659600000L, -5109987600000L,
			-5076896400000L, -5046224400000L, -5015552400000L, -4982374800000L, -4951789200000L, -4918611600000L,
			-4888026000000L, -4857440400000L, -4824262800000L, -4793677200000L, -4763005200000L, -4729827600000L,
			-4699155600000L, -4668570000000L, -4635392400000L, -4604806800000L, -4571629200000L, -4541043600000L,
			-4510458000000L, -4477280400000L, -4446608400000L, -4415936400000L, -4382758800000L, -4352173200000L,
			-4318995600000L, -4288410000000L, -4257824400000L, -4224646800000L, -4194061200000L, -4163389200000L,
			-4130211600000L, -4099626000000L, -4068954000000L, -4035862800000L, -4005277200000L, -3972099600000L,
			-3941427600000L, -3910842000000L, -3877664400000L, -3846992400000L, -3816406800000L, -3783229200000L,
			-3752643600000L, -3722058000000L, -3688880400000L, -3658208400000L, -3625030800000L, -3594445200000L,
			-3563773200000L, -3530595600000L, -3500010000000L, -3469424400000L, -3436246800000L, -3405661200000L,
			-3372483600000L, -3341811600000L, -3311226000000L, -3278048400000L, -3247376400000L, -3216790800000L,
			-3183699600000L, -3153027600000L, -3119936400000L, -3089264400000L };

	// 593年から1872年
	// index＝593年～1872年の西暦（指定年-593）、配列内（0:元号コード、1:元号コード（北朝：なければ-1）、2:月の数、3:閏月（1-12、なければ-1）、4:月並びコード
	private static final int[][] YEAR_DATA = { { 0, -1, 12, -1, 2730 }, // 593
			{ 0, -1, 13, 9, 5461 }, // 594
			{ 0, -1, 12, -1, 1370 }, // 595
			{ 0, -1, 12, -1, 2901 }, // 596
			{ 0, -1, 13, 5, 6826 }, // 597
			{ 0, -1, 12, -1, 2730 }, // 598
			{ 0, -1, 12, -1, 1370 }, // 599
			{ 0, -1, 13, 1, 2901 }, // 600
			{ 0, -1, 12, -1, 3413 }, // 601
			{ 0, -1, 13, 10, 2730 }, // 602
			{ 0, -1, 12, -1, 2733 }, // 603
			{ 0, -1, 12, -1, 1450 }, // 604
			{ 0, -1, 13, 7, 3413 }, // 605
			{ 0, -1, 12, -1, 1365 }, // 606
			{ 0, -1, 12, -1, 2733 }, // 607
			{ 0, -1, 13, 3, 5546 }, // 608
			{ 0, -1, 12, -1, 1706 }, // 609
			{ 0, -1, 13, 11, 5461 }, // 610
			{ 0, -1, 12, -1, 1366 }, // 611
			{ 0, -1, 12, -1, 2773 }, // 612
			{ 0, -1, 13, 8, 5802 }, // 613
			{ 0, -1, 12, -1, 2730 }, // 614
			{ 0, -1, 12, -1, 1366 }, // 615
			{ 0, -1, 13, 5, 2773 }, // 616
			{ 0, -1, 12, -1, 2901 }, // 617
			{ 0, -1, 12, -1, 2730 }, // 618
			{ 0, -1, 13, 2, 5462 }, // 619
			{ 0, -1, 12, -1, 1386 }, // 620
			{ 0, -1, 13, 10, 2901 }, // 621
			{ 0, -1, 12, -1, 1365 }, // 622
			{ 0, -1, 12, -1, 2731 }, // 623
			{ 0, -1, 13, 7, 5482 }, // 624
			{ 0, -1, 12, -1, 1450 }, // 625
			{ 0, -1, 12, -1, 1365 }, // 626
			{ 0, -1, 13, 4, 2731 }, // 627
			{ 0, -1, 12, -1, 2741 }, // 628
			{ 1, -1, 13, 12, 5546 }, // 629
			{ 1, -1, 12, -1, 2730 }, // 630
			{ 1, -1, 12, -1, 1365 }, // 631
			{ 1, -1, 13, 8, 2741 }, // 632
			{ 1, -1, 12, -1, 2773 }, // 633
			{ 1, -1, 12, -1, 2730 }, // 634
			{ 1, -1, 13, 5, 5461 }, // 635
			{ 1, -1, 12, -1, 1370 }, // 636
			{ 1, -1, 12, -1, 2773 }, // 637
			{ 1, -1, 13, 2, 6826 }, // 638
			{ 1, -1, 12, -1, 2730 }, // 639
			{ 1, -1, 13, 11, 5466 }, // 640
			{ 1, -1, 12, -1, 1450 }, // 641
			{ 2, -1, 12, -1, 3413 }, // 642
			{ 2, -1, 13, 7, 2730 }, // 643
			{ 2, -1, 12, -1, 2733 }, // 644
			{ 3, -1, 12, -1, 1450 }, // 645
			{ 3, -1, 13, 3, 3413 }, // 646
			{ 3, -1, 12, -1, 1365 }, // 647
			{ 3, -1, 13, 12, 2733 }, // 648
			{ 3, -1, 12, -1, 2773 }, // 649
			{ 4, -1, 12, -1, 1706 }, // 650
			{ 4, -1, 13, 9, 5461 }, // 651
			{ 4, -1, 12, -1, 1366 }, // 652
			{ 4, -1, 12, -1, 2773 }, // 653
			{ 4, -1, 13, 5, 5802 }, // 654
			{ 5, -1, 12, -1, 2730 }, // 655
			{ 5, -1, 12, -1, 1366 }, // 656
			{ 5, -1, 13, 1, 2773 }, // 657
			{ 5, -1, 12, -1, 2901 }, // 658
			{ 5, -1, 13, 10, 2730 }, // 659
			{ 5, -1, 12, -1, 2731 }, // 660
			{ 5, -1, 12, -1, 1386 }, // 661
			{ 6, -1, 13, 7, 2901 }, // 662
			{ 6, -1, 12, -1, 1365 }, // 663
			{ 6, -1, 12, -1, 2731 }, // 664
			{ 6, -1, 13, 3, 5482 }, // 665
			{ 6, -1, 12, -1, 1450 }, // 666
			{ 6, -1, 13, 11, 5461 }, // 667
			{ 6, -1, 12, -1, 1365 }, // 668
			{ 6, -1, 12, -1, 2741 }, // 669
			{ 6, -1, 13, 9, 5546 }, // 670
			{ 6, -1, 12, -1, 2730 }, // 671
			{ 7, -1, 12, -1, 1365 }, // 672
			{ 7, -1, 13, 6, 2741 }, // 673
			{ 7, -1, 12, -1, 2773 }, // 674
			{ 7, -1, 12, -1, 2730 }, // 675
			{ 7, -1, 13, 2, 5461 }, // 676
			{ 7, -1, 12, -1, 1370 }, // 677
			{ 7, -1, 13, 10, 2773 }, // 678
			{ 7, -1, 12, -1, 3413 }, // 679
			{ 7, -1, 12, -1, 2730 }, // 680
			{ 7, -1, 13, 7, 5466 }, // 681
			{ 7, -1, 12, -1, 1386 }, // 682
			{ 7, -1, 12, -1, 3413 }, // 683
			{ 7, -1, 13, 4, 2730 }, // 684
			{ 7, -1, 12, -1, 2733 }, // 685
			{ 8, -1, 13, 12, 5482 }, // 686
			{ 9, -1, 12, -1, 1706 }, // 687
			{ 9, -1, 12, -1, 1365 }, // 688
			{ 9, -1, 13, 8, 2733 }, // 689
			{ 9, -1, 12, -1, 2741 }, // 690
			{ 9, -1, 12, -1, 1706 }, // 691
			{ 9, -1, 13, 5, 6485 }, // 692
			{ 9, -1, 12, -1, 1366 }, // 693
			{ 9, -1, 12, -1, 2741 }, // 694
			{ 9, -1, 13, 2, 5802 }, // 695
			{ 9, -1, 12, -1, 1706 }, // 696
			{ 10, -1, 13, 12, 3482 }, // 697
			{ 10, -1, 12, -1, 3497 }, // 698
			{ 10, -1, 12, -1, 3474 }, // 699
			{ 10, -1, 13, 7, 7461 }, // 700
			{ 11, -1, 12, -1, 3366 }, // 701
			{ 11, -1, 12, -1, 2390 }, // 702
			{ 11, -1, 13, 4, 4789 }, // 703
			{ 12, -1, 12, -1, 2774 }, // 704
			{ 12, -1, 12, -1, 1748 }, // 705
			{ 12, -1, 13, 1, 3497 }, // 706
			{ 12, -1, 12, -1, 3785 }, // 707
			{ 13, -1, 13, 8, 3730 }, // 708
			{ 13, -1, 12, -1, 1683 }, // 709
			{ 13, -1, 12, -1, 1323 }, // 710
			{ 13, -1, 13, 6, 2391 }, // 711
			{ 13, -1, 12, -1, 2395 }, // 712
			{ 13, -1, 12, -1, 2906 }, // 713
			{ 13, -1, 13, 2, 5844 }, // 714
			{ 14, -1, 12, -1, 1876 }, // 715
			{ 14, -1, 13, 11, 5961 }, // 716
			{ 15, -1, 12, -1, 2889 }, // 717
			{ 15, -1, 12, -1, 2707 }, // 718
			{ 15, -1, 13, 7, 5419 }, // 719
			{ 15, -1, 12, -1, 1325 }, // 720
			{ 15, -1, 12, -1, 2477 }, // 721
			{ 15, -1, 13, 4, 2922 }, // 722
			{ 15, -1, 12, -1, 3498 }, // 723
			{ 16, -1, 12, -1, 2980 }, // 724
			{ 16, -1, 13, 1, 6985 }, // 725
			{ 16, -1, 12, -1, 3273 }, // 726
			{ 16, -1, 13, 9, 6805 }, // 727
			{ 16, -1, 12, -1, 2710 }, // 728
			{ 17, -1, 12, -1, 1333 }, // 729
			{ 17, -1, 13, 6, 2733 }, // 730
			{ 17, -1, 12, -1, 2773 }, // 731
			{ 17, -1, 12, -1, 3506 }, // 732
			{ 17, -1, 13, 3, 3490 }, // 733
			{ 17, -1, 12, -1, 3749 }, // 734
			{ 17, -1, 13, 11, 3402 }, // 735
			{ 17, -1, 12, -1, 1355 }, // 736
			{ 17, -1, 12, -1, 2711 }, // 737
			{ 17, -1, 13, 7, 5462 }, // 738
			{ 17, -1, 12, -1, 1370 }, // 739
			{ 17, -1, 12, -1, 2773 }, // 740
			{ 17, -1, 13, 3, 5842 }, // 741
			{ 17, -1, 12, -1, 1874 }, // 742
			{ 17, -1, 12, -1, 3749 }, // 743
			{ 17, -1, 13, 1, 5706 }, // 744
			{ 17, -1, 12, -1, 1611 }, // 745
			{ 17, -1, 13, 9, 2715 }, // 746
			{ 17, -1, 12, -1, 2733 }, // 747
			{ 17, -1, 12, -1, 1386 }, // 748
			{ 19, -1, 13, 5, 2905 }, // 749
			{ 19, -1, 12, -1, 2985 }, // 750
			{ 19, -1, 12, -1, 2898 }, // 751
			{ 19, -1, 13, 3, 6949 }, // 752
			{ 19, -1, 12, -1, 2853 }, // 753
			{ 19, -1, 13, 10, 6731 }, // 754
			{ 19, -1, 12, -1, 2645 }, // 755
			{ 19, -1, 12, -1, 2733 }, // 756
			{ 20, -1, 13, 8, 5482 }, // 757
			{ 20, -1, 12, -1, 1460 }, // 758
			{ 20, -1, 12, -1, 3497 }, // 759
			{ 20, -1, 13, 4, 7570 }, // 760
			{ 20, -1, 12, -1, 1362 }, // 761
			{ 20, -1, 13, 12, 3367 }, // 762
			{ 20, -1, 12, -1, 3367 }, // 763
			{ 20, -1, 12, -1, 2646 }, // 764
			{ 21, -1, 13, 10, 4790 }, // 765
			{ 21, -1, 12, -1, 2774 }, // 766
			{ 22, -1, 12, -1, 1748 }, // 767
			{ 22, -1, 13, 6, 3753 }, // 768
			{ 22, -1, 12, -1, 3913 }, // 769
			{ 23, -1, 12, -1, 3730 }, // 770
			{ 23, -1, 13, 3, 3366 }, // 771
			{ 23, -1, 12, -1, 3371 }, // 772
			{ 23, -1, 13, 11, 6490 }, // 773
			{ 23, -1, 12, -1, 2410 }, // 774
			{ 23, -1, 12, -1, 2778 }, // 775
			{ 23, -1, 13, 8, 5844 }, // 776
			{ 23, -1, 12, -1, 2962 }, // 777
			{ 23, -1, 12, -1, 1805 }, // 778
			{ 23, -1, 13, 5, 7819 }, // 779
			{ 23, -1, 12, -1, 3730 }, // 780
			{ 24, -1, 12, -1, 1322 }, // 781
			{ 25, -1, 13, 1, 2651 }, // 782
			{ 25, -1, 12, -1, 2411 }, // 783
			{ 25, -1, 13, 9, 3434 }, // 784
			{ 25, -1, 12, -1, 2986 }, // 785
			{ 25, -1, 12, -1, 3476 }, // 786
			{ 25, -1, 13, 5, 7493 }, // 787
			{ 25, -1, 12, -1, 3402 }, // 788
			{ 25, -1, 12, -1, 2709 }, // 789
			{ 25, -1, 13, 3, 5291 }, // 790
			{ 25, -1, 12, -1, 1326 }, // 791
			{ 25, -1, 13, 11, 1459 }, // 792
			{ 25, -1, 12, -1, 2773 }, // 793
			{ 25, -1, 12, -1, 3794 }, // 794
			{ 25, -1, 13, 7, 7588 }, // 795
			{ 25, -1, 12, -1, 3748 }, // 796
			{ 25, -1, 12, -1, 3402 }, // 797
			{ 25, -1, 13, 5, 7317 }, // 798
			{ 25, -1, 12, -1, 2710 }, // 799
			{ 25, -1, 12, -1, 1366 }, // 800
			{ 25, -1, 13, 1, 2773 }, // 801
			{ 25, -1, 12, -1, 2777 }, // 802
			{ 25, -1, 13, 10, 5842 }, // 803
			{ 25, -1, 12, -1, 1874 }, // 804
			{ 25, -1, 12, -1, 3877 }, // 805
			{ 26, -1, 13, 6, 7754 }, // 806
			{ 26, -1, 12, -1, 1354 }, // 807
			{ 26, -1, 12, -1, 3227 }, // 808
			{ 26, -1, 13, 2, 5466 }, // 809
			{ 27, -1, 12, -1, 874 }, // 810
			{ 27, -1, 13, 12, 2921 }, // 811
			{ 27, -1, 12, -1, 2985 }, // 812
			{ 27, -1, 12, -1, 2898 }, // 813
			{ 27, -1, 13, 7, 6949 }, // 814
			{ 27, -1, 12, -1, 3365 }, // 815
			{ 27, -1, 12, -1, 1613 }, // 816
			{ 27, -1, 13, 4, 4781 }, // 817
			{ 27, -1, 12, -1, 2733 }, // 818
			{ 27, -1, 12, -1, 1450 }, // 819
			{ 27, -1, 13, 1, 2917 }, // 820
			{ 27, -1, 12, -1, 3497 }, // 821
			{ 27, -1, 13, 9, 7569 }, // 822
			{ 27, -1, 12, -1, 3474 }, // 823
			{ 28, -1, 12, -1, 3366 }, // 824
			{ 28, -1, 13, 7, 2645 }, // 825
			{ 28, -1, 12, -1, 2647 }, // 826
			{ 28, -1, 12, -1, 2774 }, // 827
			{ 28, -1, 13, 3, 5553 }, // 828
			{ 28, -1, 12, -1, 1748 }, // 829
			{ 28, -1, 13, 12, 3737 }, // 830
			{ 28, -1, 12, -1, 3785 }, // 831
			{ 28, -1, 12, -1, 3729 }, // 832
			{ 28, -1, 13, 7, 3366 }, // 833
			{ 29, -1, 12, -1, 3371 }, // 834
			{ 29, -1, 12, -1, 2650 }, // 835
			{ 29, -1, 13, 5, 4826 }, // 836
			{ 29, -1, 12, -1, 2922 }, // 837
			{ 29, -1, 12, -1, 1748 }, // 838
			{ 29, -1, 13, 1, 3785 }, // 839
			{ 29, -1, 12, -1, 1865 }, // 840
			{ 29, -1, 13, 9, 5779 }, // 841
			{ 29, -1, 12, -1, 1683 }, // 842
			{ 29, -1, 12, -1, 1323 }, // 843
			{ 29, -1, 13, 7, 2395 }, // 844
			{ 29, -1, 12, -1, 1453 }, // 845
			{ 29, -1, 12, -1, 2922 }, // 846
			{ 29, -1, 13, 3, 5972 }, // 847
			{ 30, -1, 12, -1, 2980 }, // 848
			{ 30, -1, 13, 12, 6985 }, // 849
			{ 30, -1, 12, -1, 3401 }, // 850
			{ 31, -1, 12, -1, 2709 }, // 851
			{ 31, -1, 13, 8, 5421 }, // 852
			{ 31, -1, 12, -1, 1334 }, // 853
			{ 32, -1, 12, -1, 3765 }, // 854
			{ 32, -1, 13, 4, 3496 }, // 855
			{ 32, -1, 12, -1, 3538 }, // 856
			{ 33, -1, 12, -1, 3492 }, // 857
			{ 33, -1, 13, 2, 7497 }, // 858
			{ 34, -1, 12, -1, 3402 }, // 859
			{ 34, -1, 13, 10, 5781 }, // 860
			{ 34, -1, 12, -1, 2710 }, // 861
			{ 34, -1, 12, -1, 1205 }, // 862
			{ 34, -1, 13, 6, 1709 }, // 863
			{ 34, -1, 12, -1, 1749 }, // 864
			{ 34, -1, 12, -1, 3497 }, // 865
			{ 34, -1, 13, 3, 7586 }, // 866
			{ 34, -1, 12, -1, 3746 }, // 867
			{ 34, -1, 13, 12, 3398 }, // 868
			{ 34, -1, 12, -1, 3371 }, // 869
			{ 34, -1, 12, -1, 2646 }, // 870
			{ 34, -1, 13, 8, 5462 }, // 871
			{ 34, -1, 12, -1, 3418 }, // 872
			{ 34, -1, 12, -1, 3796 }, // 873
			{ 34, -1, 13, 4, 5832 }, // 874
			{ 34, -1, 12, -1, 1873 }, // 875
			{ 34, -1, 12, -1, 1699 }, // 876
			{ 35, -1, 13, 2, 5451 }, // 877
			{ 35, -1, 12, -1, 1355 }, // 878
			{ 35, -1, 13, 10, 2715 }, // 879
			{ 35, -1, 12, -1, 2733 }, // 880
			{ 35, -1, 12, -1, 1386 }, // 881
			{ 35, -1, 13, 7, 2901 }, // 882
			{ 35, -1, 12, -1, 2981 }, // 883
			{ 35, -1, 12, -1, 2898 }, // 884
			{ 36, -1, 13, 3, 6805 }, // 885
			{ 36, -1, 12, -1, 2837 }, // 886
			{ 36, -1, 13, 11, 5451 }, // 887
			{ 36, -1, 12, -1, 1365 }, // 888
			{ 37, -1, 12, -1, 2741 }, // 889
			{ 37, -1, 13, 9, 1450 }, // 890
			{ 37, -1, 12, -1, 3539 }, // 891
			{ 37, -1, 12, -1, 3492 }, // 892
			{ 37, -1, 13, 5, 7498 }, // 893
			{ 37, -1, 12, -1, 3474 }, // 894
			{ 37, -1, 12, -1, 3349 }, // 895
			{ 37, -1, 13, 1, 6477 }, // 896
			{ 37, -1, 12, -1, 1366 }, // 897
			{ 38, -1, 13, 10, 2741 }, // 898
			{ 38, -1, 12, -1, 2773 }, // 899
			{ 38, -1, 12, -1, 1748 }, // 900
			{ 39, -1, 13, 6, 3749 }, // 901
			{ 39, -1, 12, -1, 3781 }, // 902
			{ 39, -1, 12, -1, 3722 }, // 903
			{ 39, -1, 13, 3, 3350 }, // 904
			{ 39, -1, 12, -1, 3243 }, // 905
			{ 39, -1, 13, 12, 2394 }, // 906
			{ 39, -1, 12, -1, 1387 }, // 907
			{ 39, -1, 12, -1, 2922 }, // 908
			{ 39, -1, 13, 8, 5972 }, // 909
			{ 39, -1, 12, -1, 1874 }, // 910
			{ 39, -1, 12, -1, 1861 }, // 911
			{ 39, -1, 13, 5, 5771 }, // 912
			{ 39, -1, 12, -1, 2707 }, // 913
			{ 39, -1, 12, -1, 1195 }, // 914
			{ 39, -1, 13, 2, 2395 }, // 915
			{ 39, -1, 12, -1, 1453 }, // 916
			{ 39, -1, 13, 10, 2922 }, // 917
			{ 39, -1, 12, -1, 3498 }, // 918
			{ 39, -1, 12, -1, 2978 }, // 919
			{ 39, -1, 13, 6, 6981 }, // 920
			{ 39, -1, 12, -1, 3397 }, // 921
			{ 39, -1, 12, -1, 2709 }, // 922
			{ 40, -1, 13, 4, 5421 }, // 923
			{ 40, -1, 12, -1, 1334 }, // 924
			{ 40, -1, 13, 12, 1717 }, // 925
			{ 40, -1, 12, -1, 1749 }, // 926
			{ 40, -1, 12, -1, 3530 }, // 927
			{ 40, -1, 13, 8, 7586 }, // 928
			{ 40, -1, 12, -1, 3746 }, // 929
			{ 40, -1, 12, -1, 3402 }, // 930
			{ 41, -1, 13, 5, 2710 }, // 931
			{ 41, -1, 12, -1, 2711 }, // 932
			{ 41, -1, 12, -1, 1366 }, // 933
			{ 41, -1, 13, 1, 2741 }, // 934
			{ 41, -1, 12, -1, 2773 }, // 935
			{ 41, -1, 13, 11, 1746 }, // 936
			{ 41, -1, 12, -1, 851 }, // 937
			{ 42, -1, 12, -1, 1703 }, // 938
			{ 42, -1, 13, 7, 5451 }, // 939
			{ 42, -1, 12, -1, 1355 }, // 940
			{ 42, -1, 12, -1, 2715 }, // 941
			{ 42, -1, 13, 3, 6490 }, // 942
			{ 42, -1, 12, -1, 1386 }, // 943
			{ 42, -1, 13, 12, 2917 }, // 944
			{ 42, -1, 12, -1, 2985 }, // 945
			{ 42, -1, 12, -1, 2898 }, // 946
			{ 43, -1, 13, 7, 6949 }, // 947
			{ 43, -1, 12, -1, 2853 }, // 948
			{ 43, -1, 12, -1, 1613 }, // 949
			{ 43, -1, 13, 5, 2733 }, // 950
			{ 43, -1, 12, -1, 2733 }, // 951
			{ 43, -1, 12, -1, 1450 }, // 952
			{ 43, -1, 13, 1, 2985 }, // 953
			{ 43, -1, 12, -1, 3497 }, // 954
			{ 43, -1, 13, 9, 7570 }, // 955
			{ 43, -1, 12, -1, 3474 }, // 956
			{ 44, -1, 12, -1, 3365 }, // 957
			{ 44, -1, 13, 7, 6741 }, // 958
			{ 44, -1, 12, -1, 2390 }, // 959
			{ 44, -1, 12, -1, 2741 }, // 960
			{ 45, -1, 13, 3, 5556 }, // 961
			{ 45, -1, 12, -1, 1748 }, // 962
			{ 45, -1, 13, 12, 3753 }, // 963
			{ 46, -1, 12, -1, 1865 }, // 964
			{ 46, -1, 12, -1, 3731 }, // 965
			{ 46, -1, 13, 8, 3366 }, // 966
			{ 46, -1, 12, -1, 3371 }, // 967
			{ 47, -1, 12, -1, 2394 }, // 968
			{ 47, -1, 13, 5, 2774 }, // 969
			{ 48, -1, 12, -1, 2922 }, // 970
			{ 48, -1, 12, -1, 1876 }, // 971
			{ 48, -1, 13, 2, 3785 }, // 972
			{ 49, -1, 12, -1, 1865 }, // 973
			{ 49, -1, 13, 10, 5779 }, // 974
			{ 49, -1, 12, -1, 2837 }, // 975
			{ 50, -1, 12, -1, 1323 }, // 976
			{ 50, -1, 13, 7, 2651 }, // 977
			{ 51, -1, 12, -1, 1453 }, // 978
			{ 51, -1, 12, -1, 2922 }, // 979
			{ 51, -1, 13, 3, 6996 }, // 980
			{ 51, -1, 12, -1, 2980 }, // 981
			{ 51, -1, 13, 12, 6985 }, // 982
			{ 52, -1, 12, -1, 3402 }, // 983
			{ 52, -1, 12, -1, 2709 }, // 984
			{ 53, -1, 13, 8, 5421 }, // 985
			{ 53, -1, 12, -1, 1366 }, // 986
			{ 54, -1, 12, -1, 2741 }, // 987
			{ 54, -1, 13, 5, 3498 }, // 988
			{ 55, -1, 12, -1, 3538 }, // 989
			{ 56, -1, 12, -1, 3492 }, // 990
			{ 56, -1, 13, 2, 7497 }, // 991
			{ 56, -1, 12, -1, 3402 }, // 992
			{ 56, -1, 13, 10, 2710 }, // 993
			{ 56, -1, 12, -1, 2731 }, // 994
			{ 57, -1, 12, -1, 1366 }, // 995
			{ 57, -1, 13, 7, 2773 }, // 996
			{ 57, -1, 12, -1, 2793 }, // 997
			{ 57, -1, 12, -1, 1746 }, // 998
			{ 58, -1, 13, 3, 3749 }, // 999
			{ 58, -1, 12, -1, 1701 }, // 1000
			{ 58, -1, 13, 12, 3659 }, // 1001
			{ 58, -1, 12, -1, 1611 }, // 1002
			{ 58, -1, 12, -1, 2731 }, // 1003
			{ 59, -1, 13, 9, 5466 }, // 1004
			{ 59, -1, 12, -1, 1386 }, // 1005
			{ 59, -1, 12, -1, 2921 }, // 1006
			{ 59, -1, 13, 5, 5970 }, // 1007
			{ 59, -1, 12, -1, 2898 }, // 1008
			{ 59, -1, 12, -1, 2853 }, // 1009
			{ 59, -1, 13, 2, 5707 }, // 1010
			{ 59, -1, 12, -1, 1613 }, // 1011
			{ 60, -1, 13, 10, 2733 }, // 1012
			{ 60, -1, 12, -1, 2741 }, // 1013
			{ 60, -1, 12, -1, 1452 }, // 1014
			{ 60, -1, 13, 6, 2985 }, // 1015
			{ 60, -1, 12, -1, 3497 }, // 1016
			{ 61, -1, 12, -1, 3474 }, // 1017
			{ 61, -1, 13, 4, 6949 }, // 1018
			{ 61, -1, 12, -1, 3365 }, // 1019
			{ 61, -1, 13, 12, 6741 }, // 1020
			{ 62, -1, 12, -1, 2390 }, // 1021
			{ 62, -1, 12, -1, 2741 }, // 1022
			{ 62, -1, 13, 9, 5556 }, // 1023
			{ 63, -1, 12, -1, 1748 }, // 1024
			{ 63, -1, 12, -1, 3753 }, // 1025
			{ 63, -1, 13, 5, 7570 }, // 1026
			{ 63, -1, 12, -1, 3730 }, // 1027
			{ 64, -1, 12, -1, 3366 }, // 1028
			{ 64, -1, 13, 2, 6742 }, // 1029
			{ 64, -1, 12, -1, 2394 }, // 1030
			{ 64, -1, 13, 10, 2774 }, // 1031
			{ 64, -1, 12, -1, 2922 }, // 1032
			{ 64, -1, 12, -1, 1876 }, // 1033
			{ 64, -1, 13, 6, 3785 }, // 1034
			{ 64, -1, 12, -1, 1865 }, // 1035
			{ 64, -1, 12, -1, 1683 }, // 1036
			{ 65, -1, 13, 4, 5415 }, // 1037
			{ 65, -1, 12, -1, 1323 }, // 1038
			{ 65, -1, 13, 12, 2667 }, // 1039
			{ 66, -1, 12, -1, 1453 }, // 1040
			{ 66, -1, 12, -1, 3434 }, // 1041
			{ 66, -1, 13, 9, 6996 }, // 1042
			{ 66, -1, 12, -1, 2980 }, // 1043
			{ 67, -1, 12, -1, 2889 }, // 1044
			{ 67, -1, 13, 5, 6805 }, // 1045
			{ 68, -1, 12, -1, 2709 }, // 1046
			{ 68, -1, 12, -1, 1325 }, // 1047
			{ 68, -1, 13, 1, 2733 }, // 1048
			{ 68, -1, 12, -1, 2741 }, // 1049
			{ 68, -1, 13, 10, 6570 }, // 1050
			{ 68, -1, 12, -1, 3538 }, // 1051
			{ 68, -1, 12, -1, 3492 }, // 1052
			{ 69, -1, 13, 7, 7498 }, // 1053
			{ 69, -1, 12, -1, 3402 }, // 1054
			{ 69, -1, 12, -1, 2710 }, // 1055
			{ 69, -1, 13, 3, 5430 }, // 1056
			{ 69, -1, 12, -1, 1366 }, // 1057
			{ 70, -1, 13, 12, 2773 }, // 1058
			{ 70, -1, 12, -1, 2773 }, // 1059
			{ 70, -1, 12, -1, 1746 }, // 1060
			{ 70, -1, 13, 8, 3749 }, // 1061
			{ 70, -1, 12, -1, 1701 }, // 1062
			{ 70, -1, 12, -1, 1355 }, // 1063
			{ 70, -1, 13, 5, 3223 }, // 1064
			{ 71, -1, 12, -1, 2731 }, // 1065
			{ 71, -1, 12, -1, 1370 }, // 1066
			{ 71, -1, 13, 1, 2773 }, // 1067
			{ 71, -1, 12, -1, 2921 }, // 1068
			{ 72, -1, 13, 10, 6994 }, // 1069
			{ 72, -1, 12, -1, 2898 }, // 1070
			{ 72, -1, 12, -1, 2853 }, // 1071
			{ 72, -1, 13, 7, 5707 }, // 1072
			{ 72, -1, 12, -1, 1613 }, // 1073
			{ 73, -1, 12, -1, 2733 }, // 1074
			{ 73, -1, 13, 4, 5482 }, // 1075
			{ 73, -1, 12, -1, 1452 }, // 1076
			{ 74, -1, 13, 12, 2985 }, // 1077
			{ 74, -1, 12, -1, 3497 }, // 1078
			{ 74, -1, 12, -1, 3474 }, // 1079
			{ 74, -1, 13, 8, 7461 }, // 1080
			{ 75, -1, 12, -1, 3366 }, // 1081
			{ 75, -1, 12, -1, 2637 }, // 1082
			{ 75, -1, 13, 6, 4781 }, // 1083
			{ 76, -1, 12, -1, 2774 }, // 1084
			{ 76, -1, 12, -1, 1460 }, // 1085
			{ 76, -1, 13, 2, 3497 }, // 1086
			{ 77, -1, 12, -1, 3785 }, // 1087
			{ 77, -1, 13, 10, 3730 }, // 1088
			{ 77, -1, 12, -1, 3731 }, // 1089
			{ 77, -1, 12, -1, 3366 }, // 1090
			{ 77, -1, 13, 7, 2646 }, // 1091
			{ 77, -1, 12, -1, 2651 }, // 1092
			{ 77, -1, 12, -1, 858 }, // 1093
			{ 78, -1, 13, 3, 1749 }, // 1094
			{ 78, -1, 12, -1, 1877 }, // 1095
			{ 79, -1, 12, -1, 1865 }, // 1096
			{ 80, -1, 13, 1, 3731 }, // 1097
			{ 80, -1, 12, -1, 1683 }, // 1098
			{ 81, -1, 13, 9, 5419 }, // 1099
			{ 81, -1, 12, -1, 1323 }, // 1100
			{ 81, -1, 12, -1, 2667 }, // 1101
			{ 81, -1, 13, 5, 5466 }, // 1102
			{ 81, -1, 12, -1, 3434 }, // 1103
			{ 82, -1, 12, -1, 2916 }, // 1104
			{ 82, -1, 13, 2, 5961 }, // 1105
			{ 83, -1, 12, -1, 2889 }, // 1106
			{ 83, -1, 13, 10, 6805 }, // 1107
			{ 84, -1, 12, -1, 2709 }, // 1108
			{ 84, -1, 12, -1, 1325 }, // 1109
			{ 85, -1, 13, 7, 2733 }, // 1110
			{ 85, -1, 12, -1, 2741 }, // 1111
			{ 85, -1, 12, -1, 3498 }, // 1112
			{ 86, -1, 13, 3, 7076 }, // 1113
			{ 86, -1, 12, -1, 3748 }, // 1114
			{ 86, -1, 12, -1, 3402 }, // 1115
			{ 86, -1, 13, 1, 6805 }, // 1116
			{ 86, -1, 12, -1, 2710 }, // 1117
			{ 87, -1, 13, 9, 5462 }, // 1118
			{ 87, -1, 12, -1, 1370 }, // 1119
			{ 88, -1, 12, -1, 2773 }, // 1120
			{ 88, -1, 13, 5, 5586 }, // 1121
			{ 88, -1, 12, -1, 1746 }, // 1122
			{ 88, -1, 12, -1, 3749 }, // 1123
			{ 89, -1, 13, 2, 3658 }, // 1124
			{ 89, -1, 12, -1, 1611 }, // 1125
			{ 90, -1, 13, 10, 3223 }, // 1126
			{ 90, -1, 12, -1, 2731 }, // 1127
			{ 90, -1, 12, -1, 1370 }, // 1128
			{ 90, -1, 13, 7, 2901 }, // 1129
			{ 90, -1, 12, -1, 2921 }, // 1130
			{ 91, -1, 12, -1, 1874 }, // 1131
			{ 92, -1, 13, 4, 5925 }, // 1132
			{ 92, -1, 12, -1, 2853 }, // 1133
			{ 92, -1, 13, 12, 5707 }, // 1134
			{ 93, -1, 12, -1, 2645 }, // 1135
			{ 93, -1, 12, -1, 685 }, // 1136
			{ 93, -1, 13, 9, 1387 }, // 1137
			{ 93, -1, 12, -1, 1461 }, // 1138
			{ 93, -1, 12, -1, 2985 }, // 1139
			{ 93, -1, 13, 5, 6994 }, // 1140
			{ 94, -1, 12, -1, 3474 }, // 1141
			{ 95, -1, 12, -1, 3365 }, // 1142
			{ 95, -1, 13, 2, 6733 }, // 1143
			{ 96, -1, 12, -1, 2646 }, // 1144
			{ 97, -1, 13, 10, 5293 }, // 1145
			{ 97, -1, 12, -1, 726 }, // 1146
			{ 97, -1, 12, -1, 1717 }, // 1147
			{ 97, -1, 13, 6, 3497 }, // 1148
			{ 97, -1, 12, -1, 3785 }, // 1149
			{ 97, -1, 12, -1, 3730 }, // 1150
			{ 98, -1, 13, 4, 7462 }, // 1151
			{ 98, -1, 12, -1, 3370 }, // 1152
			{ 98, -1, 13, 12, 2646 }, // 1153
			{ 99, -1, 12, -1, 2651 }, // 1154
			{ 99, -1, 12, -1, 858 }, // 1155
			{ 100, -1, 13, 9, 2773 }, // 1156
			{ 100, -1, 12, -1, 1877 }, // 1157
			{ 100, -1, 12, -1, 1865 }, // 1158
			{ 101, -1, 13, 5, 3731 }, // 1159
			{ 102, -1, 12, -1, 1683 }, // 1160
			{ 103, -1, 12, -1, 1323 }, // 1161
			{ 103, -1, 13, 2, 2651 }, // 1162
			{ 104, -1, 12, -1, 2731 }, // 1163
			{ 104, -1, 13, 10, 6506 }, // 1164
			{ 105, -1, 12, -1, 3498 }, // 1165
			{ 106, -1, 12, -1, 2916 }, // 1166
			{ 106, -1, 13, 7, 5961 }, // 1167
			{ 106, -1, 12, -1, 3401 }, // 1168
			{ 107, -1, 12, -1, 2709 }, // 1169
			{ 107, -1, 13, 4, 5419 }, // 1170
			{ 108, -1, 12, -1, 1357 }, // 1171
			{ 108, -1, 13, 12, 2733 }, // 1172
			{ 108, -1, 12, -1, 2741 }, // 1173
			{ 108, -1, 12, -1, 1450 }, // 1174
			{ 109, -1, 13, 9, 3493 }, // 1175
			{ 109, -1, 12, -1, 3749 }, // 1176
			{ 110, -1, 12, -1, 3402 }, // 1177
			{ 110, -1, 13, 6, 6805 }, // 1178
			{ 110, -1, 12, -1, 2710 }, // 1179
			{ 110, -1, 12, -1, 1366 }, // 1180
			{ 111, -1, 13, 2, 2741 }, // 1181
			{ 112, -1, 12, -1, 2773 }, // 1182
			{ 112, -1, 13, 10, 6866 }, // 1183
			{ 113, -1, 12, -1, 1746 }, // 1184
			{ 114, -1, 12, -1, 3749 }, // 1185
			{ 114, -1, 13, 7, 3658 }, // 1186
			{ 114, -1, 12, -1, 1675 }, // 1187
			{ 114, -1, 12, -1, 3239 }, // 1188
			{ 114, -1, 13, 4, 5462 }, // 1189
			{ 115, -1, 12, -1, 1370 }, // 1190
			{ 115, -1, 13, 12, 2777 }, // 1191
			{ 115, -1, 12, -1, 2921 }, // 1192
			{ 115, -1, 12, -1, 1874 }, // 1193
			{ 115, -1, 13, 8, 5925 }, // 1194
			{ 115, -1, 12, -1, 2885 }, // 1195
			{ 115, -1, 12, -1, 1611 }, // 1196
			{ 115, -1, 13, 3, 5291 }, // 1197
			{ 115, -1, 12, -1, 1197 }, // 1198
			{ 116, -1, 12, -1, 1387 }, // 1199
			{ 116, -1, 13, 2, 2922 }, // 1200
			{ 117, -1, 12, -1, 2985 }, // 1201
			{ 117, -1, 13, 10, 7058 }, // 1202
			{ 117, -1, 12, -1, 3490 }, // 1203
			{ 118, -1, 12, -1, 3397 }, // 1204
			{ 118, -1, 13, 7, 6733 }, // 1205
			{ 119, -1, 12, -1, 2646 }, // 1206
			{ 120, -1, 12, -1, 1197 }, // 1207
			{ 120, -1, 13, 4, 1453 }, // 1208
			{ 120, -1, 12, -1, 1749 }, // 1209
			{ 120, -1, 12, -1, 3497 }, // 1210
			{ 121, -1, 13, 1, 7570 }, // 1211
			{ 121, -1, 12, -1, 3746 }, // 1212
			{ 122, -1, 13, 9, 7462 }, // 1213
			{ 122, -1, 12, -1, 3370 }, // 1214
			{ 122, -1, 12, -1, 2646 }, // 1215
			{ 122, -1, 13, 6, 5302 }, // 1216
			{ 122, -1, 12, -1, 858 }, // 1217
			{ 122, -1, 12, -1, 1749 }, // 1218
			{ 123, -1, 13, 2, 3785 }, // 1219
			{ 123, -1, 12, -1, 1865 }, // 1220
			{ 123, -1, 13, 10, 6803 }, // 1221
			{ 124, -1, 12, -1, 1685 }, // 1222
			{ 124, -1, 12, -1, 1323 }, // 1223
			{ 125, -1, 13, 7, 2651 }, // 1224
			{ 126, -1, 12, -1, 2731 }, // 1225
			{ 126, -1, 12, -1, 1386 }, // 1226
			{ 127, -1, 13, 3, 2901 }, // 1227
			{ 127, -1, 12, -1, 2981 }, // 1228
			{ 128, -1, 12, -1, 1865 }, // 1229
			{ 128, -1, 13, 1, 6805 }, // 1230
			{ 128, -1, 12, -1, 2709 }, // 1231
			{ 129, -1, 13, 9, 5419 }, // 1232
			{ 130, -1, 12, -1, 1357 }, // 1233
			{ 131, -1, 12, -1, 2733 }, // 1234
			{ 132, -1, 13, 6, 5482 }, // 1235
			{ 132, -1, 12, -1, 1458 }, // 1236
			{ 132, -1, 12, -1, 3493 }, // 1237
			{ 133, -1, 13, 2, 7498 }, // 1238
			{ 134, -1, 12, -1, 3402 }, // 1239
			{ 135, -1, 13, 10, 7445 }, // 1240
			{ 135, -1, 12, -1, 3238 }, // 1241
			{ 135, -1, 12, -1, 1366 }, // 1242
			{ 136, -1, 13, 7, 2869 }, // 1243
			{ 136, -1, 12, -1, 2773 }, // 1244
			{ 136, -1, 12, -1, 1746 }, // 1245
			{ 136, -1, 13, 4, 3749 }, // 1246
			{ 137, -1, 12, -1, 3749 }, // 1247
			{ 137, -1, 13, 12, 3722 }, // 1248
			{ 138, -1, 12, -1, 1675 }, // 1249
			{ 138, -1, 12, -1, 3243 }, // 1250
			{ 138, -1, 13, 9, 6486 }, // 1251
			{ 138, -1, 12, -1, 1386 }, // 1252
			{ 138, -1, 12, -1, 2906 }, // 1253
			{ 138, -1, 13, 5, 5842 }, // 1254
			{ 138, -1, 12, -1, 1874 }, // 1255
			{ 139, -1, 12, -1, 1861 }, // 1256
			{ 140, -1, 13, 3, 5771 }, // 1257
			{ 140, -1, 12, -1, 1675 }, // 1258
			{ 141, -1, 13, 10, 6315 }, // 1259
			{ 142, -1, 12, -1, 1197 }, // 1260
			{ 143, -1, 12, -1, 1387 }, // 1261
			{ 143, -1, 13, 7, 2922 }, // 1262
			{ 143, -1, 12, -1, 2986 }, // 1263
			{ 144, -1, 12, -1, 2962 }, // 1264
			{ 144, -1, 13, 4, 6981 }, // 1265
			{ 144, -1, 12, -1, 3397 }, // 1266
			{ 144, -1, 12, -1, 2709 }, // 1267
			{ 144, -1, 13, 1, 5293 }, // 1268
			{ 144, -1, 12, -1, 1205 }, // 1269
			{ 144, -1, 13, 9, 2477 }, // 1270
			{ 144, -1, 12, -1, 1749 }, // 1271
			{ 144, -1, 12, -1, 3498 }, // 1272
			{ 144, -1, 13, 5, 7586 }, // 1273
			{ 144, -1, 12, -1, 3746 }, // 1274
			{ 145, -1, 12, -1, 3398 }, // 1275
			{ 145, -1, 13, 3, 6805 }, // 1276
			{ 145, -1, 12, -1, 2646 }, // 1277
			{ 146, -1, 13, 10, 6486 }, // 1278
			{ 146, -1, 12, -1, 1370 }, // 1279
			{ 146, -1, 12, -1, 1749 }, // 1280
			{ 146, -1, 13, 7, 3786 }, // 1281
			{ 146, -1, 12, -1, 1873 }, // 1282
			{ 146, -1, 12, -1, 3747 }, // 1283
			{ 146, -1, 13, 4, 3402 }, // 1284
			{ 146, -1, 12, -1, 1355 }, // 1285
			{ 146, -1, 13, 12, 2715 }, // 1286
			{ 146, -1, 12, -1, 2733 }, // 1287
			{ 147, -1, 12, -1, 1386 }, // 1288
			{ 147, -1, 13, 10, 2901 }, // 1289
			{ 147, -1, 12, -1, 2981 }, // 1290
			{ 147, -1, 12, -1, 1873 }, // 1291
			{ 147, -1, 13, 6, 6821 }, // 1292
			{ 148, -1, 12, -1, 2837 }, // 1293
			{ 148, -1, 12, -1, 1355 }, // 1294
			{ 148, -1, 13, 2, 2731 }, // 1295
			{ 148, -1, 12, -1, 2733 }, // 1296
			{ 148, -1, 13, 10, 6506 }, // 1297
			{ 148, -1, 12, -1, 1458 }, // 1298
			{ 149, -1, 12, -1, 3497 }, // 1299
			{ 149, -1, 13, 7, 7498 }, // 1300
			{ 149, -1, 12, -1, 3466 }, // 1301
			{ 150, -1, 12, -1, 3349 }, // 1302
			{ 151, -1, 13, 4, 6477 }, // 1303
			{ 151, -1, 12, -1, 1366 }, // 1304
			{ 151, -1, 13, 12, 2741 }, // 1305
			{ 152, -1, 12, -1, 2773 }, // 1306
			{ 152, -1, 12, -1, 1748 }, // 1307
			{ 153, -1, 13, 8, 6825 }, // 1308
			{ 153, -1, 12, -1, 3781 }, // 1309
			{ 153, -1, 12, -1, 3722 }, // 1310
			{ 154, -1, 13, 6, 3366 }, // 1311
			{ 155, -1, 12, -1, 3243 }, // 1312
			{ 155, -1, 12, -1, 2390 }, // 1313
			{ 155, -1, 13, 3, 2774 }, // 1314
			{ 155, -1, 12, -1, 2922 }, // 1315
			{ 155, -1, 13, 10, 6356 }, // 1316
			{ 156, -1, 12, -1, 1877 }, // 1317
			{ 156, -1, 12, -1, 1861 }, // 1318
			{ 157, -1, 13, 7, 5771 }, // 1319
			{ 157, -1, 12, -1, 1683 }, // 1320
			{ 158, -1, 12, -1, 1323 }, // 1321
			{ 158, -1, 13, 5, 2395 }, // 1322
			{ 158, -1, 12, -1, 1453 }, // 1323
			{ 159, -1, 12, -1, 2922 }, // 1324
			{ 159, -1, 13, 1, 6996 }, // 1325
			{ 160, -1, 12, -1, 2978 }, // 1326
			{ 160, -1, 13, 9, 6981 }, // 1327
			{ 160, -1, 12, -1, 3397 }, // 1328
			{ 161, -1, 12, -1, 2709 }, // 1329
			{ 161, -1, 13, 6, 5421 }, // 1330
			{ 162, 161, 12, -1, 1205 }, // 1331
			{ 162, 172, 12, -1, 1717 }, // 1332
			{ 162, 172, 13, 2, 3498 }, // 1333
			{ 163, 163, 12, -1, 3498 }, // 1334
			{ 163, 163, 13, 10, 6562 }, // 1335
			{ 164, 163, 12, -1, 3749 }, // 1336
			{ 164, 163, 12, -1, 3402 }, // 1337
			{ 164, 173, 13, 7, 6933 }, // 1338
			{ 164, 173, 12, -1, 2710 }, // 1339
			{ 165, 173, 12, -1, 1366 }, // 1340
			{ 165, 173, 13, 4, 2741 }, // 1341
			{ 165, 174, 12, -1, 1749 }, // 1342
			{ 165, 174, 12, -1, 1746 }, // 1343
			{ 165, 174, 13, 2, 3747 }, // 1344
			{ 165, 175, 12, -1, 3749 }, // 1345
			{ 166, 175, 13, 9, 3402 }, // 1346
			{ 166, 175, 12, -1, 1355 }, // 1347
			{ 166, 175, 12, -1, 2715 }, // 1348
			{ 166, 175, 13, 6, 5466 }, // 1349
			{ 166, 176, 12, -1, 1386 }, // 1350
			{ 166, 176, 12, -1, 2917 }, // 1351
			{ 166, 177, 13, 2, 5970 }, // 1352
			{ 166, 177, 12, -1, 1874 }, // 1353
			{ 166, 177, 13, 10, 6949 }, // 1354
			{ 166, 177, 12, -1, 2853 }, // 1355
			{ 166, 178, 12, -1, 1611 }, // 1356
			{ 166, 178, 13, 7, 2859 }, // 1357
			{ 166, 178, 12, -1, 2733 }, // 1358
			{ 166, 178, 12, -1, 1450 }, // 1359
			{ 166, 178, 13, 4, 2921 }, // 1360
			{ 166, 179, 12, -1, 3497 }, // 1361
			{ 166, 180, 12, -1, 3410 }, // 1362
			{ 166, 180, 13, 1, 6949 }, // 1363
			{ 166, 180, 12, -1, 3365 }, // 1364
			{ 166, 180, 13, 9, 6733 }, // 1365
			{ 166, 180, 12, -1, 2390 }, // 1366
			{ 166, 180, 12, -1, 2741 }, // 1367
			{ 166, 181, 13, 6, 5556 }, // 1368
			{ 166, 181, 12, -1, 1748 }, // 1369
			{ 167, 181, 12, -1, 3753 }, // 1370
			{ 167, 181, 13, 3, 7570 }, // 1371
			{ 168, 181, 12, -1, 3730 }, // 1372
			{ 168, 181, 13, 10, 6438 }, // 1373
			{ 168, 181, 12, -1, 3373 }, // 1374
			{ 169, 182, 12, -1, 2390 }, // 1375
			{ 169, 182, 13, 7, 2902 }, // 1376
			{ 169, 182, 12, -1, 2922 }, // 1377
			{ 169, 182, 12, -1, 1748 }, // 1378
			{ 169, 183, 13, 4, 3785 }, // 1379
			{ 169, 183, 12, -1, 1865 }, // 1380
			{ 170, 184, 12, -1, 1683 }, // 1381
			{ 171, 184, 13, 1, 5419 }, // 1382
			{ 171, 184, 12, -1, 1323 }, // 1383
			{ 171, 185, 13, 9, 2395 }, // 1384
			{ 171, 185, 12, -1, 1453 }, // 1385
			{ 171, 185, 12, -1, 2922 }, // 1386
			{ 171, 186, 13, 5, 6996 }, // 1387
			{ 171, 186, 12, -1, 2980 }, // 1388
			{ 171, 187, 12, -1, 2885 }, // 1389
			{ 171, 188, 13, 3, 6803 }, // 1390
			{ 171, 188, 12, -1, 2709 }, // 1391
			{ 171, 188, 13, 10, 6445 }, // 1392
			{ 188, -1, 12, -1, 1366 }, // 1393
			{ 189, -1, 12, -1, 2741 }, // 1394
			{ 189, -1, 13, 7, 5930 }, // 1395
			{ 189, -1, 12, -1, 3530 }, // 1396
			{ 189, -1, 12, -1, 3492 }, // 1397
			{ 189, -1, 13, 4, 7497 }, // 1398
			{ 189, -1, 12, -1, 3402 }, // 1399
			{ 189, -1, 12, -1, 2710 }, // 1400
			{ 189, -1, 13, 1, 5421 }, // 1401
			{ 189, -1, 12, -1, 1366 }, // 1402
			{ 189, -1, 13, 10, 2741 }, // 1403
			{ 189, -1, 12, -1, 2773 }, // 1404
			{ 189, -1, 12, -1, 1746 }, // 1405
			{ 189, -1, 13, 6, 3749 }, // 1406
			{ 189, -1, 12, -1, 3749 }, // 1407
			{ 189, -1, 12, -1, 3402 }, // 1408
			{ 189, -1, 13, 3, 2710 }, // 1409
			{ 189, -1, 12, -1, 2731 }, // 1410
			{ 189, -1, 13, 10, 6490 }, // 1411
			{ 189, -1, 12, -1, 1386 }, // 1412
			{ 189, -1, 12, -1, 2921 }, // 1413
			{ 189, -1, 13, 7, 5970 }, // 1414
			{ 189, -1, 12, -1, 1874 }, // 1415
			{ 189, -1, 12, -1, 2853 }, // 1416
			{ 189, -1, 13, 5, 5707 }, // 1417
			{ 189, -1, 12, -1, 1613 }, // 1418
			{ 189, -1, 12, -1, 2731 }, // 1419
			{ 189, -1, 13, 1, 5482 }, // 1420
			{ 189, -1, 12, -1, 1450 }, // 1421
			{ 189, -1, 13, 10, 2985 }, // 1422
			{ 189, -1, 12, -1, 3497 }, // 1423
			{ 189, -1, 12, -1, 3474 }, // 1424
			{ 189, -1, 13, 6, 6949 }, // 1425
			{ 189, -1, 12, -1, 3365 }, // 1426
			{ 189, -1, 12, -1, 2645 }, // 1427
			{ 190, -1, 13, 3, 4781 }, // 1428
			{ 191, -1, 12, -1, 2741 }, // 1429
			{ 191, -1, 13, 11, 5556 }, // 1430
			{ 191, -1, 12, -1, 1748 }, // 1431
			{ 191, -1, 12, -1, 3753 }, // 1432
			{ 191, -1, 13, 7, 3730 }, // 1433
			{ 191, -1, 12, -1, 3731 }, // 1434
			{ 191, -1, 12, -1, 3366 }, // 1435
			{ 191, -1, 13, 5, 6742 }, // 1436
			{ 191, -1, 12, -1, 2394 }, // 1437
			{ 191, -1, 12, -1, 2774 }, // 1438
			{ 191, -1, 13, 1, 5844 }, // 1439
			{ 191, -1, 12, -1, 1876 }, // 1440
			{ 192, -1, 13, 9, 6857 }, // 1441
			{ 192, -1, 12, -1, 1865 }, // 1442
			{ 192, -1, 12, -1, 1683 }, // 1443
			{ 193, -1, 13, 6, 5419 }, // 1444
			{ 193, -1, 12, -1, 1323 }, // 1445
			{ 193, -1, 12, -1, 2651 }, // 1446
			{ 193, -1, 13, 2, 2906 }, // 1447
			{ 193, -1, 12, -1, 2922 }, // 1448
			{ 194, -1, 13, 10, 6484 }, // 1449
			{ 194, -1, 12, -1, 2981 }, // 1450
			{ 194, -1, 12, -1, 2889 }, // 1451
			{ 195, -1, 13, 8, 6803 }, // 1452
			{ 195, -1, 12, -1, 2709 }, // 1453
			{ 195, -1, 12, -1, 1325 }, // 1454
			{ 196, -1, 13, 4, 2733 }, // 1455
			{ 196, -1, 12, -1, 2741 }, // 1456
			{ 197, -1, 12, -1, 3498 }, // 1457
			{ 197, -1, 13, 1, 7076 }, // 1458
			{ 197, -1, 12, -1, 3492 }, // 1459
			{ 198, -1, 13, 9, 7497 }, // 1460
			{ 198, -1, 12, -1, 3402 }, // 1461
			{ 198, -1, 12, -1, 2710 }, // 1462
			{ 198, -1, 13, 6, 5422 }, // 1463
			{ 198, -1, 12, -1, 1366 }, // 1464
			{ 198, -1, 12, -1, 2773 }, // 1465
			{ 199, -1, 13, 2, 5546 }, // 1466
			{ 200, -1, 12, -1, 1746 }, // 1467
			{ 200, -1, 13, 10, 6821 }, // 1468
			{ 201, -1, 12, -1, 3749 }, // 1469
			{ 201, -1, 12, -1, 1610 }, // 1470
			{ 201, -1, 13, 8, 3223 }, // 1471
			{ 201, -1, 12, -1, 2731 }, // 1472
			{ 201, -1, 12, -1, 1338 }, // 1473
			{ 201, -1, 13, 5, 2773 }, // 1474
			{ 201, -1, 12, -1, 2921 }, // 1475
			{ 201, -1, 12, -1, 1874 }, // 1476
			{ 201, -1, 13, 1, 5797 }, // 1477
			{ 201, -1, 12, -1, 2853 }, // 1478
			{ 201, -1, 13, 9, 6731 }, // 1479
			{ 201, -1, 12, -1, 1613 }, // 1480
			{ 201, -1, 12, -1, 2733 }, // 1481
			{ 201, -1, 13, 7, 5482 }, // 1482
			{ 201, -1, 12, -1, 1452 }, // 1483
			{ 201, -1, 12, -1, 2985 }, // 1484
			{ 201, -1, 13, 3, 6994 }, // 1485
			{ 201, -1, 12, -1, 3474 }, // 1486
			{ 202, -1, 13, 11, 6949 }, // 1487
			{ 202, -1, 12, -1, 3365 }, // 1488
			{ 203, -1, 12, -1, 2645 }, // 1489
			{ 203, -1, 13, 8, 4781 }, // 1490
			{ 203, -1, 12, -1, 2741 }, // 1491
			{ 204, -1, 12, -1, 1460 }, // 1492
			{ 204, -1, 13, 4, 3497 }, // 1493
			{ 204, -1, 12, -1, 3785 }, // 1494
			{ 204, -1, 12, -1, 3730 }, // 1495
			{ 204, -1, 13, 2, 7461 }, // 1496
			{ 204, -1, 12, -1, 3366 }, // 1497
			{ 204, -1, 13, 10, 2646 }, // 1498
			{ 204, -1, 12, -1, 2651 }, // 1499
			{ 204, -1, 12, -1, 726 }, // 1500
			{ 205, -1, 13, 6, 5845 }, // 1501
			{ 205, -1, 12, -1, 1876 }, // 1502
			{ 205, -1, 12, -1, 3785 }, // 1503
			{ 206, -1, 13, 3, 3730 }, // 1504
			{ 206, -1, 12, -1, 1683 }, // 1505
			{ 206, -1, 13, 11, 5419 }, // 1506
			{ 206, -1, 12, -1, 1323 }, // 1507
			{ 206, -1, 12, -1, 2667 }, // 1508
			{ 206, -1, 13, 8, 5466 }, // 1509
			{ 206, -1, 12, -1, 3434 }, // 1510
			{ 206, -1, 12, -1, 2916 }, // 1511
			{ 206, -1, 13, 4, 5961 }, // 1512
			{ 206, -1, 12, -1, 2889 }, // 1513
			{ 206, -1, 12, -1, 2709 }, // 1514
			{ 206, -1, 13, 2, 5419 }, // 1515
			{ 206, -1, 12, -1, 1325 }, // 1516
			{ 206, -1, 13, 10, 2733 }, // 1517
			{ 206, -1, 12, -1, 2741 }, // 1518
			{ 206, -1, 12, -1, 3498 }, // 1519
			{ 206, -1, 13, 6, 7076 }, // 1520
			{ 207, -1, 12, -1, 3492 }, // 1521
			{ 207, -1, 12, -1, 3402 }, // 1522
			{ 207, -1, 13, 3, 6805 }, // 1523
			{ 207, -1, 12, -1, 2710 }, // 1524
			{ 207, -1, 13, 11, 5462 }, // 1525
			{ 207, -1, 12, -1, 1366 }, // 1526
			{ 207, -1, 12, -1, 2773 }, // 1527
			{ 208, -1, 13, 9, 5554 }, // 1528
			{ 208, -1, 12, -1, 1746 }, // 1529
			{ 208, -1, 12, -1, 3749 }, // 1530
			{ 208, -1, 13, 5, 3402 }, // 1531
			{ 209, -1, 12, -1, 1611 }, // 1532
			{ 209, -1, 12, -1, 3223 }, // 1533
			{ 209, -1, 13, 1, 5462 }, // 1534
			{ 209, -1, 12, -1, 1370 }, // 1535
			{ 209, -1, 13, 10, 2773 }, // 1536
			{ 209, -1, 12, -1, 2921 }, // 1537
			{ 209, -1, 12, -1, 1874 }, // 1538
			{ 209, -1, 13, 6, 5797 }, // 1539
			{ 209, -1, 12, -1, 2853 }, // 1540
			{ 209, -1, 12, -1, 1611 }, // 1541
			{ 209, -1, 13, 3, 3243 }, // 1542
			{ 209, -1, 12, -1, 685 }, // 1543
			{ 209, -1, 13, 11, 5483 }, // 1544
			{ 209, -1, 12, -1, 1460 }, // 1545
			{ 209, -1, 12, -1, 2985 }, // 1546
			{ 209, -1, 13, 7, 6994 }, // 1547
			{ 209, -1, 12, -1, 3474 }, // 1548
			{ 209, -1, 12, -1, 3365 }, // 1549
			{ 209, -1, 13, 5, 6733 }, // 1550
			{ 209, -1, 12, -1, 2645 }, // 1551
			{ 209, -1, 12, -1, 1197 }, // 1552
			{ 209, -1, 13, 1, 1453 }, // 1553
			{ 209, -1, 12, -1, 1461 }, // 1554
			{ 210, -1, 13, 10, 6569 }, // 1555
			{ 210, -1, 12, -1, 3785 }, // 1556
			{ 210, -1, 12, -1, 3730 }, // 1557
			{ 211, -1, 13, 6, 7461 }, // 1558
			{ 211, -1, 12, -1, 3370 }, // 1559
			{ 211, -1, 12, -1, 2646 }, // 1560
			{ 211, -1, 13, 3, 5302 }, // 1561
			{ 211, -1, 12, -1, 858 }, // 1562
			{ 211, -1, 13, 12, 5845 }, // 1563
			{ 211, -1, 12, -1, 1876 }, // 1564
			{ 211, -1, 12, -1, 3913 }, // 1565
			{ 211, -1, 13, 8, 3730 }, // 1566
			{ 211, -1, 12, -1, 1683 }, // 1567
			{ 211, -1, 12, -1, 1323 }, // 1568
			{ 211, -1, 13, 5, 2647 }, // 1569
			{ 212, -1, 12, -1, 2731 }, // 1570
			{ 212, -1, 12, -1, 1370 }, // 1571
			{ 212, -1, 13, 1, 6869 }, // 1572
			{ 213, -1, 12, -1, 2916 }, // 1573
			{ 213, -1, 13, 11, 5961 }, // 1574
			{ 213, -1, 12, -1, 2889 }, // 1575
			{ 213, -1, 12, -1, 2709 }, // 1576
			{ 213, -1, 13, 7, 5419 }, // 1577
			{ 213, -1, 12, -1, 1325 }, // 1578
			{ 213, -1, 12, -1, 2733 }, // 1579
			{ 213, -1, 13, 3, 5482 }, // 1580
			{ 213, -1, 12, -1, 1450 }, // 1581
			{ 213, -1, 12, -1, 2981 }, // 1582
			{ 213, -1, 13, 1, 6985 }, // 1583
			{ 213, -1, 12, -1, 3402 }, // 1584
			{ 213, -1, 13, 8, 6805 }, // 1585
			{ 213, -1, 12, -1, 2710 }, // 1586
			{ 213, -1, 12, -1, 1366 }, // 1587
			{ 213, -1, 13, 5, 2741 }, // 1588
			{ 213, -1, 12, -1, 2773 }, // 1589
			{ 213, -1, 12, -1, 1490 }, // 1590
			{ 213, -1, 13, 1, 3493 }, // 1591
			{ 214, -1, 12, -1, 3749 }, // 1592
			{ 214, -1, 13, 9, 3658 }, // 1593
			{ 214, -1, 12, -1, 1611 }, // 1594
			{ 214, -1, 12, -1, 3239 }, // 1595
			{ 215, -1, 13, 7, 5462 }, // 1596
			{ 215, -1, 12, -1, 1370 }, // 1597
			{ 215, -1, 12, -1, 2773 }, // 1598
			{ 215, -1, 13, 3, 5842 }, // 1599
			{ 215, -1, 12, -1, 1874 }, // 1600
			{ 215, -1, 13, 11, 5925 }, // 1601
			{ 215, -1, 12, -1, 2885 }, // 1602
			{ 215, -1, 12, -1, 1611 }, // 1603
			{ 215, -1, 13, 8, 5291 }, // 1604
			{ 215, -1, 12, -1, 1197 }, // 1605
			{ 215, -1, 12, -1, 1387 }, // 1606
			{ 215, -1, 13, 4, 2922 }, // 1607
			{ 215, -1, 12, -1, 2985 }, // 1608
			{ 215, -1, 12, -1, 2898 }, // 1609
			{ 215, -1, 13, 2, 6981 }, // 1610
			{ 215, -1, 12, -1, 3397 }, // 1611
			{ 215, -1, 13, 10, 6733 }, // 1612
			{ 215, -1, 12, -1, 2645 }, // 1613
			{ 215, -1, 12, -1, 1197 }, // 1614
			{ 216, -1, 13, 6, 1453 }, // 1615
			{ 216, -1, 12, -1, 1717 }, // 1616
			{ 216, -1, 12, -1, 3497 }, // 1617
			{ 216, -1, 13, 3, 7570 }, // 1618
			{ 216, -1, 12, -1, 3746 }, // 1619
			{ 216, -1, 13, 12, 7462 }, // 1620
			{ 216, -1, 12, -1, 3370 }, // 1621
			{ 216, -1, 12, -1, 2646 }, // 1622
			{ 216, -1, 13, 8, 5302 }, // 1623
			{ 217, -1, 12, -1, 858 }, // 1624
			{ 217, -1, 12, -1, 1749 }, // 1625
			{ 217, -1, 13, 4, 3785 }, // 1626
			{ 217, -1, 12, -1, 1865 }, // 1627
			{ 217, -1, 12, -1, 3731 }, // 1628
			{ 217, -1, 13, 2, 3370 }, // 1629
			{ 217, -1, 12, -1, 1323 }, // 1630
			{ 217, -1, 13, 10, 2647 }, // 1631
			{ 217, -1, 12, -1, 2731 }, // 1632
			{ 217, -1, 12, -1, 1386 }, // 1633
			{ 217, -1, 13, 7, 6997 }, // 1634
			{ 217, -1, 12, -1, 2916 }, // 1635
			{ 217, -1, 12, -1, 1865 }, // 1636
			{ 217, -1, 13, 3, 5779 }, // 1637
			{ 217, -1, 12, -1, 2709 }, // 1638
			{ 217, -1, 13, 11, 5419 }, // 1639
			{ 217, -1, 12, -1, 1357 }, // 1640
			{ 217, -1, 12, -1, 2733 }, // 1641
			{ 217, -1, 13, 9, 5482 }, // 1642
			{ 217, -1, 12, -1, 1458 }, // 1643
			{ 218, -1, 12, -1, 2981 }, // 1644
			{ 218, -1, 13, 5, 6986 }, // 1645
			{ 218, -1, 12, -1, 3402 }, // 1646
			{ 218, -1, 12, -1, 2837 }, // 1647
			{ 219, -1, 13, 1, 5453 }, // 1648
			{ 219, -1, 12, -1, 1366 }, // 1649
			{ 219, -1, 13, 10, 2741 }, // 1650
			{ 219, -1, 12, -1, 2773 }, // 1651
			{ 220, -1, 12, -1, 1746 }, // 1652
			{ 220, -1, 13, 6, 3493 }, // 1653
			{ 220, -1, 12, -1, 3749 }, // 1654
			{ 221, -1, 12, -1, 3722 }, // 1655
			{ 221, -1, 13, 4, 3350 }, // 1656
			{ 221, -1, 12, -1, 3239 }, // 1657
			{ 222, -1, 13, 12, 6486 }, // 1658
			{ 222, -1, 12, -1, 1370 }, // 1659
			{ 222, -1, 12, -1, 2777 }, // 1660
			{ 223, -1, 13, 8, 5842 }, // 1661
			{ 223, -1, 12, -1, 1874 }, // 1662
			{ 223, -1, 12, -1, 1861 }, // 1663
			{ 223, -1, 13, 5, 5771 }, // 1664
			{ 223, -1, 12, -1, 1675 }, // 1665
			{ 223, -1, 12, -1, 1195 }, // 1666
			{ 223, -1, 13, 2, 2395 }, // 1667
			{ 223, -1, 12, -1, 1387 }, // 1668
			{ 223, -1, 13, 10, 2922 }, // 1669
			{ 223, -1, 12, -1, 2986 }, // 1670
			{ 223, -1, 12, -1, 2962 }, // 1671
			{ 223, -1, 13, 6, 6981 }, // 1672
			{ 224, -1, 12, -1, 3397 }, // 1673
			{ 224, -1, 12, -1, 2709 }, // 1674
			{ 224, -1, 13, 4, 5293 }, // 1675
			{ 224, -1, 12, -1, 1197 }, // 1676
			{ 224, -1, 13, 12, 1453 }, // 1677
			{ 224, -1, 12, -1, 1749 }, // 1678
			{ 224, -1, 12, -1, 3498 }, // 1679
			{ 224, -1, 13, 8, 7586 }, // 1680
			{ 225, -1, 12, -1, 3746 }, // 1681
			{ 225, -1, 12, -1, 3398 }, // 1682
			{ 225, -1, 13, 5, 6805 }, // 1683
			{ 226, -1, 12, -1, 2646 }, // 1684
			{ 226, -1, 12, -1, 1370 }, // 1685
			{ 226, -1, 13, 3, 2773 }, // 1686
			{ 226, -1, 12, -1, 2921 }, // 1687
			{ 227, -1, 12, -1, 1874 }, // 1688
			{ 227, -1, 13, 1, 3749 }, // 1689
			{ 227, -1, 12, -1, 2853 }, // 1690
			{ 227, -1, 13, 8, 5707 }, // 1691
			{ 227, -1, 12, -1, 2637 }, // 1692
			{ 227, -1, 12, -1, 1195 }, // 1693
			{ 227, -1, 13, 5, 1371 }, // 1694
			{ 227, -1, 12, -1, 1453 }, // 1695
			{ 227, -1, 12, -1, 2921 }, // 1696
			{ 227, -1, 13, 2, 6994 }, // 1697
			{ 227, -1, 12, -1, 3474 }, // 1698
			{ 227, -1, 13, 9, 7461 }, // 1699
			{ 227, -1, 12, -1, 3365 }, // 1700
			{ 227, -1, 12, -1, 2645 }, // 1701
			{ 227, -1, 13, 8, 5293 }, // 1702
			{ 227, -1, 12, -1, 694 }, // 1703
			{ 228, -1, 12, -1, 1461 }, // 1704
			{ 228, -1, 13, 4, 3497 }, // 1705
			{ 228, -1, 12, -1, 3785 }, // 1706
			{ 228, -1, 12, -1, 3730 }, // 1707
			{ 228, -1, 13, 1, 7461 }, // 1708
			{ 228, -1, 12, -1, 3366 }, // 1709
			{ 228, -1, 13, 8, 2646 }, // 1710
			{ 229, -1, 12, -1, 2647 }, // 1711
			{ 229, -1, 12, -1, 726 }, // 1712
			{ 229, -1, 13, 5, 5845 }, // 1713
			{ 229, -1, 12, -1, 1748 }, // 1714
			{ 229, -1, 12, -1, 3785 }, // 1715
			{ 230, -1, 13, 2, 3730 }, // 1716
			{ 230, -1, 12, -1, 1683 }, // 1717
			{ 230, -1, 13, 10, 5419 }, // 1718
			{ 230, -1, 12, -1, 1323 }, // 1719
			{ 230, -1, 12, -1, 2651 }, // 1720
			{ 230, -1, 13, 7, 5466 }, // 1721
			{ 230, -1, 12, -1, 1386 }, // 1722
			{ 230, -1, 12, -1, 2917 }, // 1723
			{ 230, -1, 13, 4, 5961 }, // 1724
			{ 230, -1, 12, -1, 2889 }, // 1725
			{ 230, -1, 12, -1, 2709 }, // 1726
			{ 230, -1, 13, 1, 5419 }, // 1727
			{ 230, -1, 12, -1, 1325 }, // 1728
			{ 230, -1, 13, 9, 2733 }, // 1729
			{ 230, -1, 12, -1, 2741 }, // 1730
			{ 230, -1, 12, -1, 1450 }, // 1731
			{ 230, -1, 13, 5, 2981 }, // 1732
			{ 230, -1, 12, -1, 3493 }, // 1733
			{ 230, -1, 12, -1, 3402 }, // 1734
			{ 230, -1, 13, 3, 6805 }, // 1735
			{ 231, -1, 12, -1, 3222 }, // 1736
			{ 231, -1, 13, 11, 5422 }, // 1737
			{ 231, -1, 12, -1, 1366 }, // 1738
			{ 231, -1, 12, -1, 2741 }, // 1739
			{ 231, -1, 13, 7, 5554 }, // 1740
			{ 232, -1, 12, -1, 1746 }, // 1741
			{ 232, -1, 12, -1, 3749 }, // 1742
			{ 232, -1, 13, 4, 7754 }, // 1743
			{ 233, -1, 12, -1, 1610 }, // 1744
			{ 233, -1, 13, 12, 3223 }, // 1745
			{ 233, -1, 12, -1, 3243 }, // 1746
			{ 233, -1, 12, -1, 1370 }, // 1747
			{ 234, -1, 13, 10, 2774 }, // 1748
			{ 234, -1, 12, -1, 2921 }, // 1749
			{ 234, -1, 12, -1, 1874 }, // 1750
			{ 235, -1, 13, 6, 5797 }, // 1751
			{ 235, -1, 12, -1, 2853 }, // 1752
			{ 235, -1, 12, -1, 1611 }, // 1753
			{ 235, -1, 13, 2, 5275 }, // 1754
			{ 235, -1, 12, -1, 1195 }, // 1755
			{ 235, -1, 13, 11, 1387 }, // 1756
			{ 235, -1, 12, -1, 1453 }, // 1757
			{ 235, -1, 12, -1, 2985 }, // 1758
			{ 235, -1, 13, 7, 6994 }, // 1759
			{ 235, -1, 12, -1, 3474 }, // 1760
			{ 235, -1, 12, -1, 3365 }, // 1761
			{ 235, -1, 13, 4, 6731 }, // 1762
			{ 235, -1, 12, -1, 2645 }, // 1763
			{ 236, -1, 13, 12, 5293 }, // 1764
			{ 236, -1, 12, -1, 694 }, // 1765
			{ 236, -1, 12, -1, 1461 }, // 1766
			{ 236, -1, 13, 9, 3498 }, // 1767
			{ 236, -1, 12, -1, 3785 }, // 1768
			{ 236, -1, 12, -1, 3730 }, // 1769
			{ 236, -1, 13, 6, 7461 }, // 1770
			{ 236, -1, 12, -1, 3370 }, // 1771
			{ 237, -1, 12, -1, 2646 }, // 1772
			{ 237, -1, 13, 3, 5302 }, // 1773
			{ 237, -1, 12, -1, 1366 }, // 1774
			{ 237, -1, 13, 12, 1749 }, // 1775
			{ 237, -1, 12, -1, 1877 }, // 1776
			{ 237, -1, 12, -1, 1865 }, // 1777
			{ 237, -1, 13, 7, 3731 }, // 1778
			{ 237, -1, 12, -1, 1683 }, // 1779
			{ 237, -1, 12, -1, 1323 }, // 1780
			{ 238, -1, 13, 5, 2647 }, // 1781
			{ 238, -1, 12, -1, 2731 }, // 1782
			{ 238, -1, 12, -1, 1370 }, // 1783
			{ 238, -1, 13, 1, 2773 }, // 1784
			{ 238, -1, 12, -1, 2917 }, // 1785
			{ 238, -1, 13, 10, 5962 }, // 1786
			{ 238, -1, 12, -1, 2889 }, // 1787
			{ 238, -1, 12, -1, 2709 }, // 1788
			{ 239, -1, 13, 6, 5419 }, // 1789
			{ 239, -1, 12, -1, 1325 }, // 1790
			{ 239, -1, 12, -1, 2733 }, // 1791
			{ 239, -1, 13, 2, 5482 }, // 1792
			{ 239, -1, 12, -1, 1450 }, // 1793
			{ 239, -1, 13, 11, 2981 }, // 1794
			{ 239, -1, 12, -1, 3493 }, // 1795
			{ 239, -1, 12, -1, 3402 }, // 1796
			{ 239, -1, 13, 7, 7445 }, // 1797
			{ 239, -1, 12, -1, 3222 }, // 1798
			{ 239, -1, 12, -1, 2382 }, // 1799
			{ 239, -1, 13, 4, 2733 }, // 1800
			{ 240, -1, 12, -1, 2773 }, // 1801
			{ 240, -1, 12, -1, 1490 }, // 1802
			{ 240, -1, 13, 1, 3493 }, // 1803
			{ 241, -1, 12, -1, 3749 }, // 1804
			{ 241, -1, 13, 8, 3722 }, // 1805
			{ 241, -1, 12, -1, 1675 }, // 1806
			{ 241, -1, 12, -1, 3223 }, // 1807
			{ 241, -1, 13, 6, 2390 }, // 1808
			{ 241, -1, 12, -1, 1371 }, // 1809
			{ 241, -1, 12, -1, 2778 }, // 1810
			{ 241, -1, 13, 2, 5844 }, // 1811
			{ 241, -1, 12, -1, 1874 }, // 1812
			{ 241, -1, 13, 11, 5957 }, // 1813
			{ 241, -1, 12, -1, 2885 }, // 1814
			{ 241, -1, 12, -1, 2699 }, // 1815
			{ 241, -1, 13, 8, 5291 }, // 1816
			{ 241, -1, 12, -1, 1197 }, // 1817
			{ 242, -1, 12, -1, 2411 }, // 1818
			{ 242, -1, 13, 4, 2922 }, // 1819
			{ 242, -1, 12, -1, 2986 }, // 1820
			{ 242, -1, 12, -1, 2898 }, // 1821
			{ 242, -1, 13, 1, 6981 }, // 1822
			{ 242, -1, 12, -1, 3397 }, // 1823
			{ 242, -1, 13, 8, 6803 }, // 1824
			{ 242, -1, 12, -1, 2645 }, // 1825
			{ 242, -1, 12, -1, 1197 }, // 1826
			{ 242, -1, 13, 6, 2477 }, // 1827
			{ 242, -1, 12, -1, 1717 }, // 1828
			{ 242, -1, 12, -1, 3498 }, // 1829
			{ 243, -1, 13, 3, 7572 }, // 1830
			{ 243, -1, 12, -1, 3746 }, // 1831
			{ 243, -1, 13, 11, 7493 }, // 1832
			{ 243, -1, 12, -1, 3402 }, // 1833
			{ 243, -1, 12, -1, 2710 }, // 1834
			{ 243, -1, 13, 7, 5430 }, // 1835
			{ 243, -1, 12, -1, 1370 }, // 1836
			{ 243, -1, 12, -1, 2773 }, // 1837
			{ 243, -1, 13, 4, 5834 }, // 1838
			{ 243, -1, 12, -1, 1874 }, // 1839
			{ 243, -1, 12, -1, 3747 }, // 1840
			{ 243, -1, 13, 1, 3402 }, // 1841
			{ 243, -1, 12, -1, 1355 }, // 1842
			{ 243, -1, 13, 9, 2711 }, // 1843
			{ 244, -1, 12, -1, 2731 }, // 1844
			{ 244, -1, 12, -1, 1370 }, // 1845
			{ 244, -1, 13, 5, 2773 }, // 1846
			{ 244, -1, 12, -1, 2917 }, // 1847
			{ 245, -1, 12, -1, 1874 }, // 1848
			{ 245, -1, 13, 4, 5797 }, // 1849
			{ 245, -1, 12, -1, 2725 }, // 1850
			{ 245, -1, 12, -1, 1355 }, // 1851
			{ 245, -1, 13, 2, 2715 }, // 1852
			{ 245, -1, 12, -1, 2733 }, // 1853
			{ 246, -1, 13, 7, 5482 }, // 1854
			{ 246, -1, 12, -1, 1458 }, // 1855
			{ 246, -1, 12, -1, 2985 }, // 1856
			{ 246, -1, 13, 5, 6994 }, // 1857
			{ 246, -1, 12, -1, 3474 }, // 1858
			{ 246, -1, 12, -1, 3365 }, // 1859
			{ 247, -1, 13, 3, 6733 }, // 1860
			{ 248, -1, 12, -1, 2390 }, // 1861
			{ 248, -1, 13, 8, 2733 }, // 1862
			{ 248, -1, 12, -1, 2774 }, // 1863
			{ 249, -1, 12, -1, 1492 }, // 1864
			{ 250, -1, 13, 5, 3497 }, // 1865
			{ 250, -1, 12, -1, 3781 }, // 1866
			{ 250, -1, 12, -1, 3722 }, // 1867
			{ 251, -1, 13, 4, 3366 }, // 1868
			{ 251, -1, 12, -1, 3367 }, // 1869
			{ 251, -1, 13, 10, 2390 }, // 1870
			{ 251, -1, 12, -1, 2395 }, // 1871
			{ 251, -1, 12, -1, 2778 } // 1872

	};
}
