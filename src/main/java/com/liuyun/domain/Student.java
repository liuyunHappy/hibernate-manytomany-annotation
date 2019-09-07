/*
 * Copyright (c) 2019, wenwenliuyun@163.com All Rights Reserved. 
 */

package com.liuyun.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 * @author liuyun
 * @since 2019年9月4日下午10:49:08
 */
@Entity
@Table(name = "student")
public class Student implements Serializable {

	private static final long serialVersionUID = 3907174579755050980L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "stu_id")
	private Long stuId;

	@Column(name = "stu_name")
	private String stuName;
	/**
	 * mappedBy="对端的关联当前类的属性名"， 即Teacher用来关联Student类的属性名称：students.
	 * (Teacher的students属性名称)
	 */
	@ManyToMany(targetEntity = Teacher.class, mappedBy = "students")
	private Set<Teacher> teachers = new HashSet<Teacher>(0);

	public Student() {
		this(null);
	}

	public Student(String stuName) {
		this(null, stuName);
	}

	public Student(Long stuId, String stuName) {
		super();
		this.stuId = stuId;
		this.stuName = stuName;
	}

	public Long getStuId() {
		return stuId;
	}

	public void setStuId(Long stuId) {
		this.stuId = stuId;
	}

	public String getStuName() {
		return stuName;
	}

	public void setStuName(String stuName) {
		this.stuName = stuName;
	}

	public Set<Teacher> getTeachers() {
		return teachers;
	}

	public void setTeachers(Set<Teacher> teachers) {
		this.teachers = teachers;
	}

	@Override
	public String toString() {
		return "Student [stuId=" + stuId + ", stuName=" + stuName + "]";
	}

	public Student add(Teacher... teachers) {
		if (teachers == null) {
			return this;
		}
		if (this.teachers == null) {
			this.teachers = new HashSet<Teacher>(0);
		}
		for (Teacher t : teachers) {
			if (t == null) {
				continue;
			}
			this.teachers.add(t);
		}
		return this;
	}

	public Student remove(Teacher... teachers) {
		if (teachers == null) {
			return this;
		}
		if (this.teachers == null) {
			this.teachers = new HashSet<Teacher>(0);
		}
		for (Teacher t : teachers) {
			if (t == null) {
				continue;
			}
			this.teachers.remove(t);
		}
		return this;
	}
}
