/*
 * Copyright (c) 2019, wenwenliuyun@163.com All Rights Reserved. 
 */

package com.liuyun.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 * @author liuyun
 * @since 2019年9月4日下午10:48:14
 */
@Entity
@Table(name = "teacher")
public class Teacher implements Serializable {
	private static final long serialVersionUID = -4629543766422581183L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tch_id")
	private Long tchId;

	@Column(name = "tch_name")
	private String tchName;

	/*
	 * 1: @ManyToMany：配置多对多关系
	 * 	cascade：级联
	 * 	mappedBy：
	 * 	fetch：是否同时查询关联表
	 * 2: @JoinTable定义中间表
	 * 	name：中间表的表名
	 * 	joinColumns：中间表与本类的表的字段关系
	 * 	inverseJoinColumns：中间表与对端表的字段关系
	 */
	@ManyToMany(targetEntity = Student.class, cascade = CascadeType.ALL)
	@JoinTable(name = "tch_stu_ref", joinColumns = {
			@JoinColumn(name = "tch_id", referencedColumnName = "tch_id") }, inverseJoinColumns = {
					@JoinColumn(name = "stu_id", referencedColumnName = "stu_id") })
	private Set<Student> students = new HashSet<Student>(0);

	public Teacher() {
		this(null);
	}

	public Teacher(String tchName) {
		this(null, tchName);
	}

	public Teacher(Long tchId, String tchName) {
		super();
		this.tchId = tchId;
		this.tchName = tchName;
	}

	public Long getTchId() {
		return tchId;
	}

	public void setTchId(Long tchId) {
		this.tchId = tchId;
	}

	public String getTchName() {
		return tchName;
	}

	public void setTchName(String tchName) {
		this.tchName = tchName;
	}

	public Set<Student> getStudents() {
		return students;
	}

	public void setStudents(Set<Student> students) {
		this.students = students;
	}

	@Override
	public String toString() {
		return "Teacher [tchId=" + tchId + ", tchName=" + tchName + "]";
	}

	public Teacher add(Student... students) {
		if (students == null) {
			return this;
		}
		if (this.students == null) {
			this.students = new HashSet<Student>(0);
		}
		for (Student s : students) {
			if (s == null) {
				continue;
			}
			this.students.add(s);
		}
		return this;
	}

	public Teacher remove(Student... students) {
		if (students == null) {
			return this;
		}
		if (this.students == null) {
			this.students = new HashSet<Student>(0);
		}
		for (Student s : students) {
			if (s == null) {
				continue;
			}
			this.students.remove(s);
		}
		return this;
	}
}
