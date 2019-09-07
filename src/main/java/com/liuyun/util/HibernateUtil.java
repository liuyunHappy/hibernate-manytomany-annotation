/*
 * Copyright (c) 2019, wenwenliuyun@163.com All Rights Reserved. 
 */

package com.liuyun.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * @author liuyun
 * @since 2019年9月3日下午7:34:51
 */
public class HibernateUtil {
	private static SessionFactory factory;
	static {
		factory = new Configuration().configure().buildSessionFactory();
	}

	public static Session openSession() {
		return factory.openSession();
	}

	/**
	 * 获取当前线程的session：若当前线程没有Session则创建一个，并记录到当前线程
	 * 
	 * @author liuyun
	 * @return
	 * @since 2019年9月3日下午7:41:07
	 */
	public static Session getCurrentSession() {
		Session session = factory.getCurrentSession();
		if (session == null) {
			session = factory.openSession();
		}
		return session;
	}
}
