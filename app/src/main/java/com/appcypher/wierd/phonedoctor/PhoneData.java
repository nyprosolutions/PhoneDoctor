package com.appcypher.wierd.phonedoctor;

import android.util.Pair;

import java.util.List;

/**
 * Created by Nypro on 27-Mar-17.
 */

public class PhoneData {
	public int coverIconResource;
	public String coverCategory;
	public String coverBriefDetail;
	public int detailIconResource;
	public List<Pair<String, String>> detail; // title and detail


	public PhoneData(
		int coverIconResource, String coverCategory, String coverBriefDetail,
		int detailIconResource, List<Pair<String, String>> detail){

		this.coverIconResource = coverIconResource;
		this.coverCategory = coverCategory;
		this.coverBriefDetail = coverBriefDetail;
		this.detailIconResource = detailIconResource;
		this.detail = detail;
	}
}
