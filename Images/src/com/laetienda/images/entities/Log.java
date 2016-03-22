package com.laetienda.images.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;


/**
 * The persistent class for the logs database table.
 * 
 */
@Entity
@Table(name="logs")
@NamedQuery(name="Log.findAll", query="SELECT l FROM Log l")
public class Log implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "logs_id_seq", sequenceName = "logs_id_seq", allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "logs_id_seq")
	@Column(name="id", updatable=false)
	private Integer id;

	@Column(name="class", nullable=true, unique=false)
	private String class_;
	
	@Column(name="\"created\"", insertable = false, updatable = false, nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;
	
	@Column(name="\"level\"", nullable=true, unique=false)
	private String level;
	
	@Column(name="\"line\"", nullable=true, unique=false)
	private Integer line;
	
	@Column(name="\"log\"", nullable=true, unique=false)
	private String log;
	
	@Column(name="\"method\"", nullable=true, unique=false)
	private String method;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="\"userId\"")
	private User user;

	public Log() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getClass_() {
		return this.class_;
	}

	public void setClass_(String class_) {
		this.class_ = class_;
	}

	public Date getCreated() {
		return this.created;
	}

	public String getLevel() {
		return this.level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public Integer getLine() {
		return this.line;
	}

	public void setLine(Integer line) {
		this.line = line;
	}

	public String getLog() {
		return this.log;
	}

	public void setLog(String log){
		
		if(log.length() > 254){
			this.log = log.substring(0, 254);
		}else{
			this.log = log;
		}
	}

	public String getMethod() {
		return this.method;
	}

	public void setMethod(String method) {
		this.method = method;
	}
	
	public User getUser(){
		return user;
	}
	
	public void setUser(User user){
		this.user = user;
	}

}