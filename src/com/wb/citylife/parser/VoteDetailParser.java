package com.wb.citylife.parser;


import com.wb.citylife.bean.VoteDetail;
import com.wb.citylife.bean.VoteDetail.QuestionItem;
import com.wb.citylife.config.VoteType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class VoteDetailParser {
	public VoteDetail parse(String resultStr) {		
		
		Gson gson = new Gson();
		VoteDetail data = gson.fromJson(resultStr, new TypeToken<VoteDetail>(){}.getType());
		
		for(QuestionItem qItem : data.datas) {
			if(!qItem.hasVote) break;
			
			if(qItem.questionType == VoteType.VOTE_TYPE_SINGLE ||
					qItem.questionType == VoteType.VOTE_TYPE_MULTIPLE) {
				qItem.rate = new int[qItem.result.length];
				int total = 0;
				for(int i=0; i<qItem.result.length; i++) {
					qItem.rate[i] = Integer.parseInt(qItem.result[i]);
					total += qItem.rate[i];
				}
				for(int i=0; i<qItem.rate.length; i++) {
					qItem.rate[i] = qItem.rate[i] * 100 / total;
				}
			}
		}
		
		return data;
	}
}