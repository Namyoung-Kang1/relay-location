/**
 * 프로젝트명		: ASolution Framework
 * 개발사			: 어솔루션- <a href="http://www.asolution.biz/">asolution.biz</a>
 * 연락처			: 070-8257-1349
 *
 * 프로그램명		: Email
 * 프로그램설명		:
 **/

package com.lucis.relaylocation.util;

import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.HashMap;

@JsonRootName("result")
public class JsonResult extends HashMap<String, Object> {

	private static final long serialVersionUID = 1L;
	
	public JsonResult() {
		super.put("code", 0);
		super.put("msg", "SUCCESS");
	}

	public void setResult(int code, String msg) {
		super.put("code", code);
		super.put("msg", msg);
	}
	
	public void setResult(int code, String msg, String msgDtl) {
		super.put("code", code);
		super.put("msg", msg);
		super.put("msgDtl", msgDtl);
	}
	
	public void add(String key, Object val) {
		super.put(key, val);
	}

}