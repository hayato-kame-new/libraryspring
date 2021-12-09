package com.kame.springboot.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;

import com.kame.springboot.entity.Book;
import com.kame.springboot.entity.History;

@Controller
public class HistoryController {  // 貸し出しに関するコントローラ
	
	 /**
     * 本のインスタンスがキー その本に関する情報としてその本の今までの貸出記録が値
     */
    Map<Book, List<History>> historyMap = new HashMap<Book, List<History>>();
    
   
    
    
	

}
