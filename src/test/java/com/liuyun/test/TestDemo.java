/*
 * Copyright (c) 2019, wenwenliuyun@163.com All Rights Reserved. 
 */

package com.liuyun.test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;

import com.liuyun.domain.Student;
import com.liuyun.domain.Teacher;
import com.liuyun.util.HibernateUtil;

/**
 * @author liuyun
 * @since 2019年9月3日下午7:42:44
 */
public class TestDemo {
	@Test
	public void testSave() {
		Session session = null;
		Transaction trans = null;
		try {
			session = HibernateUtil.getCurrentSession();
			trans = session.beginTransaction();
			// 构造对象
			Teacher t1 = new Teacher("t1");
			Teacher t2 = new Teacher("t2");
			Student s1 = new Student("s1");
			Student s2 = new Student("s2");
			Student s3 = new Student("s3");
			//设置关联关系
			t1.add(s1, s2);
			t2.add(s2, s3);			
			s1.add(t1);
			s2.add(t1,t2);
			s3.add(t2);

			session.save(t1); // teacher表开启了级联保存
			session.save(t2);

			trans.commit();
		} catch (Exception e) {
			trans.rollback();
			throw e;
		}
	}

	/**
	 * 直接通过HQL批量删时，没有触发级联删除
	 * 
	 * @author liuyun
	 * @since 2019年9月5日下午10:21:21
	 */
	@Test
	public void testDeleteTeacherByHQL() {
		Session session = null;
		Transaction trans = null;
		try {
			session = HibernateUtil.getCurrentSession();
			trans = session.beginTransaction();
			String property = "tchName";
			session.createQuery("delete " + Teacher.class.getName() + " where " + property + " = :" + property)
					.setParameter(property, "t1").executeUpdate();
			trans.commit();
		} catch (Exception e) {
			trans.rollback();
			throw e;
		}
	}

	/**
	 * 直接通过HQL批量删时，没有触发级联删除
	 * 
	 * @author liuyun
	 * @since 2019年9月5日下午10:21:21
	 */
	@Test
	public void testDeleteTeacherBySQL() {
		Session session = null;
		Transaction trans = null;
		try {
			session = HibernateUtil.getCurrentSession();
			trans = session.beginTransaction();
			String property = "tchName";
			session.createSQLQuery("delete from teacher where tch_name = :" + property).setParameter(property, "t2")
					.executeUpdate();
			trans.commit();
		} catch (Exception e) {
			trans.rollback();
			throw e;
		}
	}

	/**
	 * 先查再删除才有级联的效果：因为删除的student被另一个teacher关联，故删除失败（此处只开启单向的级联teacher-->student，没有开启反向级联）
	 * 
	 * @author liuyun
	 * @since 2019年9月5日下午10:20:40
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testDeleteTeacherByName() {
		Session session = null;
		Transaction trans = null;
		try {
			session = HibernateUtil.getCurrentSession();
			trans = session.beginTransaction();
			List<Teacher> list = session.createCriteria(Teacher.class).add(Restrictions.eq("tchName", "t1")).list();
			if (!list.isEmpty()) {
				for (Teacher teacher : list) {
					session.delete(teacher);
				}
			}
			trans.commit();
		} catch (Exception e) {
			trans.rollback();
			throw e;
		}
	}

	/**
	 * 按步骤删除teacher：t1 根据t1查找到Teacher ，【然后根据tch_id查找中间表M1s；遍历根据中间表查找student表】，
	 * 记录没有关联还有其他teacher的student列表S1（根据stu_id查找不在M1s列表中的中间表M2s，若M2s为空，则该student可以删除）
	 * 然后：删除M1s，删除t1，删除S1
	 * 
	 * @author liuyun
	 * @since 2019年9月5日下午10:20:40
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testDeleteTeacherByNameBySteps() {
		Session session = null;
		Transaction trans = null;
		try {
			session = HibernateUtil.getCurrentSession();
			trans = session.beginTransaction();
			String tchName = "t1";
			// 获取Teacher，校验存在性
			List<Teacher> list = session.createCriteria(Teacher.class).add(Restrictions.eqOrIsNull("tchName", tchName))
					.list();
			Teacher t = !list.isEmpty() ? list.get(0) : null;
			if (t == null) {
				System.out.println("Teacher named '" + tchName + "' is not exist!!");
				return;
			}

			// 根据tch_id查找中间表中的stu_id
			List<Long> stuIds = session.createSQLQuery(
					"SELECT ss.stu_id FROM ( SELECT stu_id FROM tch_stu_ref WHERE tch_id = :tchId ) AS ss "
							+ "WHERE ( SELECT COUNT(*) FROM tch_stu_ref AS tt WHERE tt.stu_id = ss.stu_id ) = 1")
					.setParameter("tchId", t.getTchId()).list();
			System.out.println(stuIds);

			// 删除匹配的中间表数据
			session.createSQLQuery("delete from tch_stu_ref where tch_id=:tchId ").setParameter("tchId", t.getTchId())
					.executeUpdate();
			// 删除匹配的teacher表数据
			session.createSQLQuery("delete from teacher where tch_id=:tchId").setParameter("tchId", t.getTchId())
					.executeUpdate();
			// 删除匹配的student表数据
			String formaterInData = stuIds.toString().replace('[', '(').replace(']', ')');
			session.createSQLQuery("delete from student where stu_id in" + formaterInData).executeUpdate();
			trans.commit();
		} catch (Exception e) {
			trans.rollback();
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testQueryAll() {
		Session session = null;
		Transaction trans = null;
		try {
			session = HibernateUtil.getCurrentSession();
			trans = session.beginTransaction();
			List<Teacher> teachers = session.createCriteria(Teacher.class).list();
			if (!teachers.isEmpty()) {
				Set<Student> students = teachers.get(0).getStudents();
				System.out.println(students);
			}
			List<Student> students = session.createCriteria(Student.class).list();
			if (!students.isEmpty()) {
				Set<Teacher> ts = students.get(0).getTeachers();
				System.out.println(ts);
			}
			List<Object[]> middles = session.createSQLQuery("select * from tch_stu_ref").list();
			System.out.println("teacher    数量=" + teachers.size() + " ...  " + teachers);
			System.out.println("student    数量=" + students.size() + " ...  " + students);
			System.out.print("tch_stu_ref数量=" + middles.size() + " ...  ");
			for (Object[] arr : middles) {
				System.out.print(Arrays.toString(arr) + "  ");
			}
			trans.commit();
		} catch (Exception e) {
			trans.rollback();
			throw e;
		}
	}

	@Test
	public void testDeleteAll() {
		Session session = null;
		Transaction trans = null;
		try {
			session = HibernateUtil.getCurrentSession();
			trans = session.beginTransaction();
			session.createQuery("delete " + Teacher.class.getName()).executeUpdate();
			session.createQuery("delete " + Student.class.getName()).executeUpdate();
			session.createSQLQuery("delete from tch_stu_ref").executeUpdate();
			trans.commit();
		} catch (Exception e) {
			trans.rollback();
			throw e;
		}
	}
}
