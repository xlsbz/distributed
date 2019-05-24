package com.dhr.shop.utils;

import java.util.Random;

/**
 * 
 * @ClassName: UUIDUtils
 * @Description: TODO(随机数)
 * @author Mr DU
 * @date 2019年3月30日
 *
 */
public class UUIDUtils {

	/**
	 * @Title: getImageName @Description: (图片名随机生成) @param @return @return
	 *         String @throws
	 */
	public static String getImageName() {
		// 1.获取当前时间 毫秒
		long longTime = System.currentTimeMillis();
		// 2.生成三维随机数
		Random random = new Random();
		int end = random.nextInt(999);
		// 不够填0
		String str = String.format("%03d", end);
		return longTime + str;
	}

	/**
	 * @Title: getItemId @Description: TODO(商品ID生成) @param @return @return
	 *         String @throws
	 */
	public static Long getItemId() {
		long longTime = System.currentTimeMillis();
		int end = new Random().nextInt(99);
		return new Long(longTime + String.format("%02d", end));
	}

}
