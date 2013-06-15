var androidnga={}
androidnga.cache = {}

function loadTitle(ss,fs) {
    window.__SMALL_SCREEN=ss;
    window.__FONT_SIZE=fs;
}
function loadAvatar(avatar,lou) {
        if(avatar && avatar.length != 0){
            var photo = document.getElementById("photo_"+lou);
            photo.src=avatar;
            var bigphoto = document.getElementById("bigphoto_"+lou);
            bigphoto.src=avatar;
        }
}
function loadInfo(lou,type,floor,author,authorid,level,aurvrc,postnum,postdate) {
        /*if(!content)
            return;
        var obj;
        try{
            obj = eval('('+content+')');
        }catch (e){
              //obj = JSON.parse(content,function (key, value) {        
              //return key.indexOf('js_escap_avatar') >= 0 ? '' : value;    });
              try{
              obj = eval('('+content.replace(/"{.*?}"/g,'\"\"')+')');
              }catch (e){
              	console.log(e);
              	return;
              }
        }*/
        var gp_lesser;
        if ((type & 1)!==0) {
        	   return;
        }
        var d = document.getElementById('div_'+lou);
        d.style.display='';
        document.getElementById("floor_"+lou).innerHTML="#"+floor;
        document.getElementById("author_"+lou).innerHTML=author + ": ";
        document.getElementById("authorname_"+lou).innerHTML=author;
        document.getElementById("authorid_"+lou).innerHTML=authorid;
        document.getElementById("level_"+lou).innerHTML=level;
        document.getElementById("aurvrc_"+lou).innerHTML=aurvrc;
        document.getElementById("postnum_"+lou).innerHTML=postnum;
        var format='y-m-d H:i';
        document.getElementById("postdate_"+lou).innerHTML=commonui.time2dis(postdate,format);
        //var pc = document.getElementById("postcontent"+lou);
        //pc.innerHTML=content;
        //var al = document.getElementById("alterinfo"+lou);
        //al.innerHTML=obj.alterinfo;
        if (gp_lesser) {
        	  d.style.backgroundImage='url(sikle_bg.gif)';
            //p.parentNode.parentNode.parentNode.style.backgroundImage='url(sikle_bg.gif)';
        } else if(aurvrc < 0) {
        	  d.style.backgroundImage='url(skeleton_bg.gif)';
            //p.parentNode.parentNode.parentNode.style.backgroundImage='url(skeleton_bg.gif)';
        }
        //ps.parentNode.innerHTML+="<img src='about:blank' class='x' onerror='contentDisp(\"postcontent"+lou+"\",\"postsigncontent"+lou+"\",\"posterinfo"+lou
        //+"\","+lou+",0,"+obj.content_length+","+obj.gp_lesser+",null,"+obj.tid+","+obj.pid+","+obj.authorid+",null,null,null,null,\"\",null,null,null,null,"+obj.fid+",null,\"\",0,null,null,\"postsubject"+lou+"\","+obj.type+",\"alterinfo"+lou+"\")'>";
}
function loadContent(content,alertinfo,signature,attachs,comment,lou,cLen,/*lesser,*/rvrc,tid,pid,aid,type,showSign){
    if(!content)
        return;
    var cC = document.getElementById("postcontent_"+lou);
    if (!cC)
  	return;
  	var lesser;
  	if (lesser) {
        cC.parentNode.className=' pc5';
    }
  	cC.innerHTML=content;
  	cC.style.fontSize=window.__FONT_SIZE;
    if(type){
	  var cS = document.getElementById("postsubject_"+lou);
	  if ((type & 1024)!==0)
		cS.innerHTML+="<span class='red nobr' title='无法编辑/回复'>[锁定]</span>";
	  if ((type & 2)!==0)
		cS.innerHTML+="<span class='red nobr' title='只有作者/版主可见'>[隐藏]</span>";

	  if ((type & 1)!==0)//comment
		return;
	  }

    if(!cLen){
	  cLen = content.length;
	  }
    if (content && content.substr(0,24).indexOf('lessernuke')>-1)
  	  cLen=0;

    var cA = document.getElementById("alterinfo_"+lou);
    if(cA && alertinfo){
    	cA.style.fontSize=window.__FONT_SIZE;
	  var alterlen = alertinfo.length;
	  if(alterlen>0) {
		if(cA.parentNode.style.display=='none')
			cA.parentNode.style.display=''
	  cA.innerHTML=commonui.loadAlertInfo(alertinfo)
	  }else{
		cA.parentNode.style.display='none'
	  }
    }

	  var cSig = document.getElementById("postsigncontent_"+lou);
    if (cSig && signature){
    if(showSign && cLen>30){
		    if(cSig.parentNode.style.display=='none')
			      cSig.parentNode.style.display=''
		    var lite=window.__SMALL_SCREEN?true:false
		    commonui.loadPostSign(cSig,0,lesser,rvrc,1,tid,pid,aid,lite,signature)
	  }else{
		    cSig.parentNode.style.display='none'
	  }
	  }

    var postattach = document.getElementById('postattach_'+lou);
    if(postattach && attachs){
    	  var obj;
        try {
          obj = eval('('+attachs+')');
          postattach.style.fontSize=window.__FONT_SIZE;
	        postattach.parentNode.style.display='';
		      var x='';
		      for (var item in obj) {
	           var d = obj[item];
			       x+=this.genattach(d.attachurl,d.name,d.type,d.thumb,d.dscp,d.size,d.url_utf8_org_name);
			    }
			    if(window.__SMALL_SCREEN){
			    	var id = Math.random()+'__';
                                androidnga.cache[id] = x;
			    	postattach.innerHTML="<button type='button' onclick='this.nextSibling.innerHTML=androidnga.cache[\""+id+"\"];this.nextSibling.style.display=\"\";this.style.display=\"none\"'>点击显示附件</button><span style='display:none'></span>";
			    }else{
			      postattach.innerHTML=x;
			    }
        } catch (e) {
        }
    }
    var cmt = document.getElementById("comment_"+lou);
    if(cmt && comment) {
    	var obj;
        try{
            obj = eval('('+comment+')');
            var x = '';
            for(var item in obj) {
              var d = obj[item];
              x+="<div class='quote'><span><b>"+d.author+":</b></span>";
              x+="<span><span id=\"postcommentsubject"+d.pid+"\"></span><span id=\"postcomment"+d.pid+"\" class=\" ubbcode\">"+d.content+"</span></span></div>";
		          //x+="<div><b>"+d.author+"</b>";
		          //x+="<table><tbody><tr><td class=\"b9tl\"></td><td class=\"b9t\"></td><td class=\"b9tr\"></td></tr><tr><td class=\"b9lcc\"></td><td class=\"b9c comment2\">";
		          //x+="<span><span id=\"postcommentsubject"+d.pid+"\"></span><span id=\"postcomment"+d.pid+"\" class=\" ubbcode\">"+d.content+"</span></span>";
		          x+="<img src=\"about:blank\" class=\"x\" onerror='commonui.postDisp(\"postcomment"+d.pid+"\",null,null,0,0,0,0,0,"+d.tid+",\""+d.pid+"\","+d.authorid+",null,null,0,0,null,null,0,null,0,0,0,null,null,null,null,\"postcommentsubject"+d.pid+"\",1,null)'>";
		          //x+="</td><td class=\"b9r\"></td></tr><tr><td class=\"b9bl\"></td><td class=\"b9b\"></td><td class=\"b9br\"></td></tr></tbody></table></div><br>";
            }
            cmt.style.fontSize=window.__FONT_SIZE;
            cmt.innerHTML+=x;
            cmt.style.display='';
        } catch (e) {
        	 console.log(e);
        }
    }
    ubbcode.bbscode(cC,0,lesser,rvrc,0,tid,pid,aid)
}//fe
function genattach(url,name,type,thumb,dscp,size,url_utf8_org_name){
        var orglink='',main='',del='';
        var u = commonui.getAttachBase(url)+"/"+url;

        if(type == 'img'){
	          if (thumb==1){
                //orglink = "[<a target='_blank' href='"+commonui.getAttachBase(url)+"/"+url+"' target='_blank'>查看原图</a>]";
	          	  main = "<a href='javascript:void(0)' title='点击查看原图' onclick=\"window.jsInterface.showImg('"+u+"')\" ><img src='"+u+".thumb.jpg' alt=''/></a>";
        	      if(window.__SMALL_SCREEN){
                  var id = Math.random()+'_';
                  androidnga.cache[id] = main;
        	  	    main = "<button type='button' onclick='this.nextSibling.innerHTML=androidnga.cache[\""+id+"\"];this.nextSibling.style.display=\"\";this.style.display=\"none\"'>点击显示缩略图</button><span style='display:none'></span>";
        	      }
  	        } else {
		            main = "<button type='button' onclick=\"window.jsInterface.showImg('"+u+"')\">点击显示图片</button>";
		        }
	          return "<span class='left quote'>"+dscp+"<br>"+main+"</span>";
	          //return "<table class='left quote'><tr><td>"+dscp+"<div class='clear'></div>"+main+"<div class='clear'></div>"+orglink+" "+del+"</td></tr></table>";
	      } else {
	      	  u = u+(url_utf8_org_name ? '?filename='+decodeURIComponent(url_utf8_org_name) : '');
	          return "<span class='left quote'>"+dscp+"<br><a href='"+u+"' onclick=\"window.jsInterface.openUrl('"+u+"')\" target='_blank' class='green'>"+(url_utf8_org_name ? decodeURIComponent(url_utf8_org_name) : name)+"</a> ("+size+" K) "+del+"</span>";
	          //return "<table class='quote'><tr><td>"+dscp+" <a href='"+u+(url_utf8_org_name ? '?filename='+decodeURIComponent(url_utf8_org_name) : '')+"' target='_blank' class='green'>"+(url_utf8_org_name ? decodeURIComponent(url_utf8_org_name) : name)+"</a> ("+size+" K) "+del+"</td></tr></table>";
	      }
}