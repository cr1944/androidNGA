if (!window.ubbcode)var ubbcode={}
if (!window.__IMGPATH)var __IMGPATH = 'http://img4.ngacn.cc/ngabbs'

ubbcode.smiles = {
1:'a1.gif',
2:'a2.gif',
3:'a3.gif',
4:'a4.gif',
5:'a5.gif',
6:'a6.gif',
7:'a7.gif',
8:'a8.gif',
32:'a32.gif',
33:'a33.gif',
34:'a34.gif',
30:'a30.gif',
29:'a29.gif',
28:'a28.gif',
27:'a27.gif',
26:'a26.gif',
25:'a25.gif',
24:'a24.gif',
35:'a35.gif',
36:'a36.gif',
37:'a37.gif',
38:'a38.gif',
39:'a39.gif',
40:'a40.gif',
41:'a41.gif',
42:'a42.gif',
43:'a43.gif'
}

ubbcode.checkLinkTable = {
'worldofwarcraft.com':1,
'ofcard.com':1,
'uusee.com':1,
'youtube.com':1,
'youku.com':1,
'weplay.cn':1,
'tudou.com':1,
'ngacn.cc':-1,
'uencn.com':1,
'sc2.cc':1,
'ngacn.com':1,
'wowchina.com':1,
'microsoft.com':1,
'dmzj.com':1,
'178.com':-1,
'com.cn':'add1',
'pixiv.net':'add1',
'embed.pixiv.net':1,
'loli.my':'add1',
'static.loli.my':1,
'sina.com.cn':1,
'bilibili.us':1,
'bilibili.tv':1
}

ubbcode.checkIframeTable = {
0:'http://embed.pixiv.net/code.php?id=',
1:'http://wow.178.com/'
}

ubbcode.regexplock = 0
ubbcode.videonum = 0
ubbcode.bbscodeConvArgsSave = {}

ubbcode.secure_text=function(t)
{
t = t.replace(/</g,'&lt;');
t = t.replace(/>/g,'&gt;');
t = t.replace(/"/g,'&quot;');
t = t.replace(/'/g,'&#39;');
t = t.replace(/&lt;br\/?&gt;/gi,'<br />');
return t;
}
//fe

/*
var arg = {
	c:,
	txt:,
	noImg:,
	tId:,
	pId:,
	authorId:,
	rvrc:,
	isSig:,
	callBack:,
	isLesser:
	}
*/
ubbcode.bbsCode=function(arg){
if (this.regexplock){
	var self = this
	window.setTimeout(function(){self.bbsCode(arg)},50);
	}
else{
	this.regexplock = 1;
	if (typeof(arg.c)=='string')arg.c=document.getElementById(arg.c)
	arg.tId = parseInt(arg.tId,10)
	if(!arg.tId)arg.tId=0
	arg.pId = parseInt(arg.pId,10)
	if(!arg.pId)arg.pId=0
	arg.authorId = parseInt(arg.authorId,10)
	if(!arg.authorId)arg.authorId=0
	var argsId = arg.c.id ? arg.c.id : 'bbcode'+Math.random()
	arg.argsId = argsId
	this.bbscodeConvArgsSave[argsId]=arg
	if(arg.c.className.indexOf('ubbcode')==-1)arg.c.className+=' ubbcode'
	arg.c.innerHTML = this.bbscode_core(arg);
	if(arg.callBack)arg.callBack(arg)
	this.regexplock = 0;
	}
}//fe

ubbcode.bbscode=function(o,noimg,tid,pid,uid)
{
var arg = {
	c:o,
	noImg:noimg,
	tId:tid,
	pId:pid,
	authorId:uid
	}
this.bbsCode(arg)
}

ubbcode.bbscode_core=function(arg)
{
var c = arg.txt ? arg.txt : arg.c.innerHTML

if (c.match(/[\[=&]/)==null)
	return c
c = c.replace(/\n/g,' ');
c = this.secure_text(c)

c = this.codeTag.parse(c)

if(c.indexOf('&')!=-1){
    c = c.replace(/&amp;#(\d{2,6});/g,function($0,$1){
        if($1>127)
            return  String.fromCharCode($1)
        else
            return  $0
        })
    }

c = this.bbscode_common(c,arg.noImg,arg.argsId)



c = c.replace(/p_w_upload/gi,"attachment");//[img]
return c;
}//fe

function nextElement(obj){
var next = obj.nextSibling;
while (next && next.nodeType != 1)
	next = next.nextSibling;
return next;
}
//fe

function prevElement(obj){
var prev = obj.previousSibling;
while (prev && prev.nodeType != 1)
	prev = prev.previousSibling;
return prev;
}
//fe

function findNameInNeighbor(o,n){
o = o.parentNode;
return findNameInChild(o,n);
}
//fe

function findNameInChild(o,n){
for (var i=0; i<o.childNodes.length;i++){
	if (o.childNodes[i].getAttribute && o.childNodes[i].getAttribute('name') == n){
			return o.childNodes[i];
		}
	}
}//fe
ubbcode.manualLoadCache = {}

ubbcode.bbscode_common=function(c,noimg,argsId)
{

if(window.__SMALL_SCREEN && !noimg)noimg = 1

var self = this

c = c.replace(/\[crypt\](.+?)\[\/crypt\]/gi,function($0,$1){
	var id = 'crypt'+Math.random()
	window.ubbcode.decryptCache[id] = $1;
	return "<div class='quote'><b>输入正确的密码以浏览加密的内容 </b><input type=text size=10><button type='button' onclick='ubbcode.decrypt(this.previousSibling.value,ubbcode.decryptCache[\""+id+"\"],this.parentNode,\""+argsId+"\")'>确认</button></div>"
	})

c = c.replace(/(?:<br\s*\/?>)?\s*\[table\]\s*(?:<br\s*\/?>)*\s*(.+?)\s*(?:<br\s*\/?>)*\s*\[\/table\]\s*(?:<br\s*\/?>)?/gi,function ($0,$1){
	var t = $1
	t = t.replace(/(?:<br\s*\/?>)*\s*\[tr\]\s*(?:<br\s*\/?>)*\s*(.+?)\s*(?:<br\s*\/?>)*\s*\[\/tr\]\s*(?:<br\s*\/?>)?/gi,"<tr>$1</tr>");//[tr]
	t = t.replace(/(?:<br\s*\/?>)*\s*\[td\s*([^\]]*)\]\s*(?:<br\s*\/?>)?\s*(.*?)\s*(?:<br\s*\/?>)?\s*\[\/td\]\s*(?:<br\s*\/?>)*/gi,function($0,$1,$2){
		var x,w='',c='',r='';
		if (parseFloat($1))
			w = 'width:'+$1+'%;'
		else{
			if (x = $1.match(/width=?(\d{0,2})/))
				w='width:'+x[1]+'%;'
			if (x = $1.match(/colspan=?(\d{0,2})/))
				c=' colspan='+x[1]+' '
			if (x = $1.match(/rowspan=?(\d{0,2})/))
				r=' rowspan='+x[1]+' '
			}
		return "<td "+c+r+" style='"+w+"border-left:1px solid #aaa;border-bottom:1px solid #aaa'>"+$2+"</td>"
		}
	);//[td]
	return "<div><table cellspacing='0px' style='border:1px solid #aaa;width:99.9%'>"+t+"</table></div>"
	}
);//[table]


c = c.replace(/\[del\](.+?)\[\/del\]/gi,"<del class='gray'> $1 </del>");//[del]
c = c.replace(/\[u\](.+?)\[\/u\]/gi,"<u>$1</u>");//[u]
c = c.replace(/\[b\](.+?)\[\/b\]/gi,"<b>$1</b>");//[b]
c = c.replace(/\[i\](.+?)\[\/i\]/gi,"<i style='font-style:italic'>$1</i>");//[i]

c = c.replace(/\[dice\]([\dd+\s]+?)\[\/dice\]/gi,function($0,$1){
var sum = 0;
var rr = $1;
$1 = '+'+$1;
$1 = $1.replace(/(\+)(\d{0,10})(?:(d)(\d{1,10}))?/gi,function($0,$1,$2,$3,$4){
	if($2)$2 = parseInt($2,10); else if($3)$2=1; else $2=0;
	$4 = parseInt($4,10);
	var r = '';
	if(!$3){if(typeof(sum)!='string')sum+=$2;return '+'+$2}
	if($2>10 || $4>1000){sum='ERROR';return'+OUT OF LIMIT'};
	for (var i=0; i<$2; i++)
		{
		var rand = Math.floor(ubbcode.sRand.rnd(argsId)*$4)+1
		r+='+d'+$4+'('+rand+')';
		if(typeof(sum)!='string')sum+=rand;
		}
	return r;
	});
return "<table class='dice'><tr><td><b>ROLL : "+rr+'</b>='+$1.substr(1)+'=<b>'+sum+"</b></td></tr></table>";
});//[dice]

if(c.indexOf('[list')!=-1){
	while (c.match(/\[list(?:=.)?\](?:.*?)\[\/list\]/)){
		c = c.replace(/(.*)\[list(=.)?\](.*?)\[\/list\]\s*(?:<br\s*\/?>)?/i,function ($0,$1,$2,$3){
			$1 = $1.replace(/<br\s*\/?>$/i,'')
			$3 = $3.split('[*]')
			var l = ''
			for(var i=0;i<$3.length;i++){
				$3[i] = $3[i].replace(/^<br\s*\/?>|<br\s*\/?>$/ig,'')
				if($3[i])
					l += '<li>' + $3[i] + '</li>'
				}
			if($2)
				return $1 + "<ol type"+$2+">" + l +"</ol>"
			else
				return $1 + "<ul>" + l +"</ul>"
			});
		}
	}//[list]

if(c.indexOf('[album')!=-1){
	c = c.replace(/\[album=?([^\]]{0,50})\](.+?)\[\/album\]/gi,function ($0,$1,$2){
		return "[quote]"+$2+"[/quote]";
		});//[album\]
	}//if

//c = c.replace(/\[quote\](.+?)\[\/quote\]/gi,"<div class='quote'>$1</div>");//[quote]
c = ubbcode.parseQuote.parse(c)
//c = c.replace(/\[say\](.+?)\[\/say\]/gi,"<div class='say'>$1</div>");//[say]

c = c.replace(/\[lessernuke\](.+?)\[\/lessernuke\]/gi,"<div class='lessernuke'><span class='crimson'>用户因此贴被暂时禁言，此效果不会累加</span> <a href='javascript:void(0)' onclick='nextElement(this).style.display=\"block\"'>点击查看</a><div style='display:none'>$1</div></div>");//[lessernuke]

c = c.replace(/\[color=(skyblue|royalblue|blue|darkblue|orange|orangered|crimson|red|firebrick|darkred|green|limegreen|seagreen|teal|deeppink|tomato|coral|purple|indigo|burlywood|sandybrown|sienna|chocolate|silver|gray)\](.+?)\[\/color\]/gi,"<span class='$1'>$2</span>");//[color]

c = c.replace(/\[email\](.+?)\[\/email\]/gi,"<a href='mailto:$1'>$1</a>");//[email\]

c = c.replace(/\[size=(\d{1,3})%?\](.*?)\[\/size\]/gi,function($0,$1,$2){return "<span style='font-size:"+$1+"%;line-height:183%'>"+$2+"</span>"});//[size\]

c = c.replace(/\[font=(simsun|simhei|Arial|Arial Black|Book Antiqua|Century Gothic|Comic Sans MS|Courier New|Georgia|Impact|Tahoma|Times New Roman|Trebuchet MS|Script MT Bold|Stencil|Verdana|Lucida Console)\](.+?)\[\/font\]/gi,"<span style='font-family:$1'>$2</span>");//[font]

c = c.replace(/(?:<br\s*\/?>)?\s*(\[align=)(left|center|right)(\])(.+?)(\[\/align\])\s*(?:<br\s*\/?>)?/gi,"<div style='text-align:$2'>$4</div>");//[align]

if(c.indexOf('[headline]')!=-1){
var tmp = []
c = c.replace(/(?:<br\s*\/?>)*\s*\[headline\](.+?)\[\/headline\]\s*(?:<br\s*\/?>)*/ig,function ($0,$1){tmp.push($1);if(tmp.length==1)return '[headlinehere]';else return ''});
c = c.replace('[headlinehere]',function (){
var x = tmp
if(x){
	var td1,td2
	var z =[]
	for (var k=0;k<x.length;k++)
		{
		var u = x[k].match(/\[url=(https?|ftp|gopher|news|telnet|mms|rtsp|)(.+?)\](.+?)\[\/url\]/i)
		if(u){
			if(!u[1])u[1]='http://';
			u=self.writelink(u[1]+u[2],u[3])
			}
		else{
			u = x[k].match(/\[url\](https?|ftp|gopher|news|telnet|mms|rtsp)(.+?)\[\/url\]/i)
			if(u){
				u=self.writelink(u[1]+u[2],u[1]+u[2])
				}
			else
				{
				u = x[k].match(/\[(tid|pid)=?(\d{0,20})\](.+?)\[\/(tid|pid)\]/i)
				if(u){
					if(u[2])
						u= self.writelink('/read.php?'+u[1]+'='+u[2],u[3])
					else
						u= self.writelink('/read.php?'+u[1]+'='+u[3],u[3])
					}
				else
					{
					u = x[k].match(/\[hltxt\](.+?)\[\/hltxt\]/i)
					if(u)
						u=u[1]
					}
				}
			}
		if(u)u='<span class=hltxt>'+u+'</span>'
		var i = x[k].match(/\[img\](.+?)\[\/img\]/i)
		if(i){
			if (noimg)
				u = self.writelink(i[1],i[1]), i=null
			else
				i=i[1], z._Img=1
			}
		z.push({'i':i,'u':u})
		}
	if(!self.loadHeadLineElm)self.loadHeadLineElm = []
	self.loadHeadLineElm.push(z)

	return '<div></div><img style="display:none" src="about:blank" onerror="ubbcode.loadHeadLine(this.previousSibling,'+(self.loadHeadLineElm.length-1)+')"/>'
	}
});
}

var img_count = 0;



/*if(c.indexOf('[album')!=-1){
	c = c.replace(/\[album=?([^\]]{0,50})\](.+?)\[\/album\]/gi,function ($0,$1,$2){
		var img=[]
		if ($2.match(/\[(?:img|url)\]/i))
			$2.replace(/\]\s*((?:(?:https?:\/\/)|(?:\.\/))[^\[]+)\s*\[/ig,function ($0,$1){if($1.substr(0,2)=='./')$1=commonui.getAttachBase($1)+'/'+$1.substr(2);img.push($1)})
		else
			$2.replace(/(?:^|[^a-zA-Z0-9\-_\+=\.\$;\/\?:@&=#%])((?:https?:\/\/|\.\/)[a-zA-Z0-9\-_\+=\.\$;\/\?:@&=#%]+)/ig,function($0,$1){if($1.substr(0,2)=='./')$1=commonui.getAttachBase($1)+'/'+$1.substr(2);img.push($1)})
		if (img[0]){
			var a = img[0]
			if (a && a.substr(0,2)=='./')
				a = commonui.getAttachBase(a)+'/'+a.substr(2);
			if (commonui.ifUrlAttach(a) && a.substr(a.length-10)!='.thumb.jpg')
				a+='.thumb.jpg'
			if (!$1)
				$1='查看相册'
			var arg = self.bbscodeConvArgsSave[argsId]
			var tid = parseInt(arg.tId,10)
			var pid = parseInt(arg.pId,10)
			var id = self.album.albumCount++
			if(!tid)tid=0
			if(!pid)pid=0
			ubbcode.album.cache[id]={album:img,title:$1,tid:tid,pid:pid,id:id}
			return "<table class='quote album'><tr><td><a href='./nuke/album.html?uid="+__CURRENT_UID+"&tid="+tid+"&pid="+pid+"&id="+id+"' target='_blank' onclick='ubbcode.album.open(\""+id+
				"\")'>"+$1+"<br/><img src='about:blank' onerror='"+(noimg?'':"ubbcode.album.imgLoad(this,\""+a+"\")")+"' style='display:none'/><br/>共"+img.length+"张图片 点击查看全部</a></td></tr></table>"
			}
		return "[album="+$1+"]"+$2+"[/album]"
		});//[album\]
	}//if*/

if(noimg!=2){
	c = c.replace(/\[img(\d{0,3})\](.+?)\[\/img\]/gi,function($0,$1,$2){
		if($2.match(/^[\x00-\x7F]+$/)) 
			var src=$2; 
		else
			var src='';
		if (src && src.substr(0,2).toLowerCase()=='./')
			src = commonui.getAttachBase(src)+'/'+src.substr(2);
		if(src && src.substr(0,7).toLowerCase()!='http://') 
			src = 'http://'+src;
		if($1 && $1<=100 && $1>0)
			$1="style='width:"+($1-0.1)+"%'";
		else
			//$1="style='' class='imgmaxwidth' onload='ubbcode.adjImgSize(this)'";
			$1="style='' class='imgmaxwidth'";
			
		if (commonui.ifUrlAttach(src))
			{
			if (src.substr(src.length-10)=='.thumb.jpg')
				return("<a href='"+src.substr(0,src.length-10)+"' class='thumblink' target='_blank'><img src='"+src+"' onerror='this.onerror=null;this.parentNode.title=\"\";this.src=\""+src.substr(0,src.length-10).replace(/&/g,'&amp;')+"\"'/></a>")
			img_count++
			if (img_count>50)
				return("<a href='"+src+"' title='为了保证正常浏览，暂不显示全部大图' class='thumblink' target='_blank'><img src='"+src+".thumb.jpg' onerror='this.onerror=null;this.parentNode.title=\"\";this.src=\""+src.replace(/&/g,'&amp;')+"\"'/></a>")
			}
		else{
			src=src.replace(/^http:\/\/db1?\.178\.com\//i,'http://img.db.178.com/')
		}

		//if(src.match(/ngacn\.cc/gi))return "为了保证正常浏览，暂不显示签名中的图片";
		var isemo = 0;
		for (i=0;i<MojoEmoArr.length;i++){
			for(j=0;j<MojoEmoArr[i].img.length;j++){
				var emo = MojoEmoArr[i].img[j];
			  if (src == emo){
				  isemo = 1;
					console.log(emo);
					var u = src.split('/');
				  src=MojoEmoArr[i].id+'/'+u[u.length-1];
				  break;
			  }
			}
		}

		var x = "<img "+$1+" src='"+src+"' alt='' onerror='this.nextSibling.style.display=\"inline\"'/><span class='silver' style='display:none'> [ "+$2+" ] </span>"
		if(!isemo && noimg){
			var id = 'id'+Math.random()
			self.manualLoadCache[id]=x
			return "<button type='button' onclick='this.nextSibling.innerHTML=ubbcode.manualLoadCache[\""+id+"\"];this.nextSibling.style.display=\"\";this.style.display=\"none\"'>点击显示图片</button><span style='display:none'></span>"
			}
		else
			return x;
	/*
			return "<button type='button' onclick='this.nextSibling.onerror=function(){this.nextSibling.style.display=\"inline\"};this.nextSibling.src=\""+src.replace(/&/g,'&amp;')+"\";this.nextSibling.style.display=\"\";this.style.display=\"none\"'>点击显示图片</button><img "+$1.replace("style='","style='display:none;")+" src='about:blank' alt=''/><span class='silver' style='display:none'> [ "+$2+" ] </span>"

			return "<img "+$1+" src='"+src+"' alt='' onerror='this.nextSibling.style.display=\"inline\"'/><span class='silver' style='display:none'> [ "+$2+" ] </span>"
			*/
		});//[img]

	if(c.indexOf('[flash')!=-1){
		c = c.replace(/\[flash\](.+?)\[\/flash\]/gi,function($0,$1){
			var w=480, h=400, u=$1.replace('.bilibili.us','.bilibili.tv').replace(/(is)?AutoPlay=.+?(&|$)/ig,'')
			if(u.substr(0,7).toLowerCase()!='http://')
				u = 'http://'+u
				return "<button type='button' onclick=\"window.jsInterface.showFlash('"+u+"')\" >点击显示Flash</button>";
			/*if (self.checklink(u,1)!=1)
				return $0
			if(u.indexOf('pl.bilibili.tv')!=-1 || u.indexOf('static.loli.my')!=-1)
				w=542,h=412
			self.videonum++
			var x = "<object class='video' width='"+w+"' height='"+h+"' classid='clsid:D27CDB6E-AE6D-11cf-96B8-444553540000' codebase='http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,0,0'><param name='movie' value='"+u+"'><embed src='"+u+"' quality='high' type='application/x-shockwave-flash' width='"+w+"' height='"+h+"' allowfullscreen='true'></embed></object>"
			if(noimg || self.videonum>1){
				var id = 'id'+Math.random()
				self.manualLoadCache[id]=x
				return "<button type='button' onclick='this.nextSibling.innerHTML=ubbcode.manualLoadCache[\""+id+"\"];this.nextSibling.style.display=\"\";this.style.display=\"none\"'>点击显示Flash</button><span style='display:none'></span>"
				}
			else
				return x*/
			} );//[flash]
		}//if

	/*if(c.indexOf('[iframe')!=-1){
		c = c.replace(/\[iframe=(\d+),(\d+)\](https?)(.+?)\[\/iframe\]/gi,function($0,$1,$2,$3,$4){
			$1 = parseInt($1,10);$2 = parseInt($2,10)
			$1 = (!$1 || $1>1000)? 500 : $1
			$2 = (!$2 || $2>2000)? 500 : $2
			var u = $3+$4,x=$0
			for (var k in self.checkIframeTable)
				if(u.indexOf(self.checkIframeTable[k])==0)
					x= "<div style='border:1px solid #444;width:"+$1+"px;height:"+($2+10)+"px;overflow:hidden'><div style='font-size:8px;line-height:10px;height:10px;overflow:hidden'>IFRAME:"+u+"</div><iframe frameborder=0 marginheight=0 marginwidth=0 scrolling='no' style='border:none;width:"+$1+"px;height:"+$2+"px;overflow:hidden;margin:0;background:transparent' src='"+u+"'></iframe></div>";

			if (noimg){
				var id = 'id'+Math.random()
				self.manualLoadCache[id]=x
				return "<button type='button' onclick='this.nextSibling.innerHTML=ubbcode.manualLoadCache[\""+id+"\"];this.nextSibling.style.display=\"\";this.style.display=\"none\"'>点击显示页面</button><span style='display:none'></span>"
				}
			else
				return x
			});//[iframe]
		}//if*/

	//if(noimg){}
	//else
		c = c.replace(/\[s:(\d{1,11})\]/gi,function($0,$1){ return("<img src='"+self.smiles[$1]+"' alt=''/>") } );//[smile]


}//noimg

c = c.replace(/\[attach\](.+?)\[\/attach\]/gi,function($0,$1){
	if ($1 && $1.substr(0,2).toLowerCase()=='./')
		$1 = commonui.getAttachBase($1)+'/'+$1.substr(2);
	if (commonui.ifUrlAttach($1))
		return self.writelink($1,$1)
	});



c = c.replace(/\[url=(https?|ftp|gopher|news|telnet|mms|rtsp|)(.+?)\](.+?)\[\/url\]/gi,function($0,$1,$2,$3){if(!$1)$1='http://';return(self.writelink($1+$2,$3))});//[url]

c = c.replace(/\[url\](https?|ftp|gopher|news|telnet|mms|rtsp)(.+?)\[\/url\]/gi,function($0,$1,$2){return(self.writelink($1+$2,$1+$2))});//[url]

c = c.replace(/\[(tid|pid)=?([\d,]{0,50})\](.+?)\[\/\1\]/gi,function($0,$1,$2,$3){
	return self.postLinkTag($1,$2,$3)
	})

  c = c.replace(/\[customachieve\](.+?)\[\/customachieve\]/gi,function($0,$1){
	//if (is_signature && customachievecount)
	//	return $0;
	var i = $1.match(/\[img\](.+?)\[\/img\]/i);
	var t = $1.match(/\[title\](.{1,40}?)\[\/title\]/i);
	var x = $1.match(/\[txt\](.{1,100}?)\[\/txt\]/i);
	if (!i)
		i={0:0,1:'achieve_default.jpg'};
	else
		i[1]=i[1].replace(/^http:\/\/db1?\.178\.com\//i,'http://img.db.178.com/')
	if (t && x){
		return "<div style='width:100%;height:74px;background-image:url(achieve_bg.png); background-repeat: no-repeat;font-size:12px;text-align:center;overflow:hidden;color:#000'>"
    +"<h2 style='display:block;font-size:14px;line-height:22px;height:22px;color:#fff;margin:0px 0px 5px 0px'>"+t[1]+"</h2>"+x[1]+"</div>";
		}	else
		return $0;
	});

if(c.indexOf('armory')!=-1){

c = c.replace(/\[(us|cn|tw|eu)armory (.{1,50}?) ([^ ]{1,50}?)\s*\]/gi,function($0,$1,$2,$3){
	  $2=$2.replace("'",'&#39;')
	  $3=$3.replace("'",'&#39;')
		return "<span><span><span><b>["+$2+" "+$3+"]</b></span></span></span><img src='about:blank' style='display:none' onerror='ubbcode.getarmory(\""+$2+"\",\""+$3+"\",this.previousSibling.childNodes[0].childNodes[0].childNodes[0],\""+$1.toLowerCase()+"\",-1)'/>"
	});
	//[armory]

}//if

c = c.replace(/\[(item|spell|quest|npc|achieve)(=[^\]]{1,20})?\](.{1,100}?)\[\/(item|spell|quest|npc|achieve)\]/gi,function($0,$1,$2,$3,$4){
if ($1!=$4)return $0
if($2)$2 = $2.substr(1)
return ubbcode.dbLinkGen('[wow,'+$1+($2 ? ','+$2 : '')+'['+$3+']]')
});//[db]

c = c.replace(/\[@(.{2,20}?)\]/gi,function($0,$1){
	 var u = "http://bbs.ngacn.cc/nuke.php?func=ucp&charset=UTF-8&username="+encodeURIComponent($1);
	 return" <a href='"+u+"' class='b' onclick=\"window.jsInterface.openUrl('"+u+"')\">"+$0+"</a> "
	 } );//[@]

c = c.replace(/(?:<br\s*\/?>)?\s*\[h\](.*?)\[\/h\]\s*(?:<br\s*\/?>)?/gi,function($0,$1){if($1){return "<h4>"+$1+"</h4>"}else{return "<h4 class='subtitle' style='line-height:0;font-size:0;padding:0;margin:0 0 3px 0;height:0'></h4>"}});//[h]
c = c.replace(/(<br\s*\/?>)?\s*(={3,100})([\u0000-\uffff]+?)(={3,100})\s*(<br\s*\/?>)?/gi,
	function($0,$1,$2,$3,$4,$5){
		if ($3.length<=100 && !$3.match(/<br\s*\/?>/i)){
			if($3=='=')$3='';
			$2 = "<h4 class='subtitle'>";
			$4 = "</h4>";
			$1=$5='';
			}
		if (!$1)$1=''
		if (!$2)$2=''
		if (!$3)$3=''
		if (!$4)$4=''
		if (!$5)$5=''
		return $1+$2+$3+$4+$5});//[h]

c = c.replace(/(<br\s*\/?>)?\s*(={6,100})\s*(<br\s*\/?>)?/gi,
	function($0,$1,$2,$3){
			$2 = "<h4 class='subtitle'></h4>";
			$1=$3='';
		return $1+$2+$3});//[h]



c = c.replace(/(?:<br\/>)?\s*\[(l|r)(\d*)\]\s*(?:<br\/>)?\s*(.+?)\s*(?:<br\/>)?\s*\[\/\1\]\s*(?:<br\/>)?/gi,function($0,$1,$2,$3){
	if($2 && $2>0 && $2<=100)
		$2="style='overflow:hidden;width:"+($2-0.1)+"%'"
	else
		$2=''
	if($1=='l')
		$1='left'
	else
		$1='right'
	return "<div "+$2+" class='"+$1+"'>"+$3+"</div>"
	});//[left][right]

c = c.replace(/p_w_upload/gi,"attachment");//[img]

c = c.replace(/\[collapse(=[^\]]{1,50})?\]\s*(?:<br\s*\/?>)*\s*(.+?)\s*(?:<br\s*\/?>)*\s*\[\/collapse\]/gi,function($0,$1,$2){
	if ($1)$1='<b class="gray">'+$1.substr(1)+' ...</b>'
	else $1 = '<b class="gray">点击显示隐藏的内容 ...</b>'
	return "<div style='border-top:1px solid #fff;border-bottom:1px solid #fff' class='collapse_btn'><button style='font-size:12px;line-height:normal;padding:0px 2px;font-weight:bold' onclick='this.parentNode.style.display=\"none\";this.parentNode.nextSibling.style.display=\"block\";' type='button' name='collapseButton'>+</button> "+$1+"</div><div style='border-top:1px solid #fff;border-bottom:1px solid #fff;display:none' class='collapse_content'>"+$2+"</div>"
	});

c = c.replace(/\[t\.178\.com\/(.+?)\]/,function($0,$1){
	if($1.substr(0,1)=='#'){
		var arg = self.bbscodeConvArgsSave[argsId]
		if (arg.isSig)return;
		var x = arg.c,b=null
		while (x!=document.body){
			x=x.parentNode
			if(x.className.match(/c\d+/)){
				b=commonui.rgbToHex(commonui.getStyle(x,'background-color')).substr(1)
				break}
			}
		var x = _$('<a/>')._.css('display','none')
		arg.c.appendChild(x)
		var a = commonui.rgbToHex(commonui.getStyle(x,'color')).substr(1),c = commonui.rgbToHex(commonui.getStyle(arg.c,'color')).substr(1),u ="http://t.178.com/widget/topic/publish?keyword="+encodeURIComponent($1.substr(1,$1.length-2))+"&height=400&color="+b+','+c+','+a+"&number=20"
		arg.c.removeChild(x)
		return "<div style='border:1px solid #444;border-left:none;border-right:none;width:90%;height:645px;overflow:hidden'><iframe frameborder=0 marginheight=0 marginwidth=0 scrolling='no' style='border:none;width:100%;height:635px;overflow:hidden;margin:0;background:transparent' src='"+u+"'></iframe><div style='font-size:8px;line-height:10px;height:10px;overflow:hidden'>IFRAME:"+u+"</div></div>";
		} 
	var id = '_t178com'+Math.random();
	httpDataGetter.script_muti_get('http://t.178.com/api/nga/get_tweets?js=1&user_id='+$1,
		function(r){
			if(r.result==false)
				$(id).innerHTML = '<span class=silver>[t.178.com:ERROR:' + r.code + ' ' + r.reason+']</span>'
			else{
				if(r.data)
					$(id).innerHTML =  self.bbscode_t178_tpl(r.data[0],null,$1)
				else
					$(id).innerHTML =  "<span class=silver>[t.178.com/"+$1+":ERROR:无内容]</span>"
					
				}
			return true
			},
		function(){
			$(id).innerHTML = '<span class=silver>[t.178.com:ERROR:无法获取数据]</span>'
			}
		);
	return "<span id='"+id+"'></span>"
	});

c = c.replace(/\[audit\]待审核\[\/audit\]/gi,function($0){
	var arg = self.bbscodeConvArgsSave[argsId]
	return "<a href='javascript:void(0)' onclick='commonui.audit(event,"+arg.tId+","+arg.pId+")' class='b red'>[待审核]</a>"
	});//[img]

if(c.indexOf('[randomblock]')!=-1){
	var x = [], did = 'randomblock'+Math.floor(Math.random()*10000);
	c = c.replace(/(?:\s*<br\s*\/?>\s*)*\s*\[randomblock]\s*(?:\s*<br\s*\/?>\s*)*(.+?)(?:\s*<br\s*\/?>\s*)*\s*\[\/randomblock]\s*(?:\s*<br\s*\/?>\s*)*/gi,function($0,$1){
		var id = did+x.length
		x.push(id)
		return "<div style='border-top:1px solid #fff;border-bottom:1px solid #fff;display:none' id='"+id+"'><button style='font-size:12px;line-height:normal;padding:0px 2px;float:left;font-weight:bold' onclick='ubbcode.randomBlock.show(\""+did+"\",1)' title='显示所有的随机内容' type='button' name='randomblockButton'>+</button>"+$1+"</div>"
		})
	if(x.length){
		c+="<img src='about:blank' style='display:none' onerror='ubbcode.randomBlock.show(\""+did+"\")'/>";
		self.randomBlock.all[did] = x
		}
	}
return c;
}//fe

ubbcode.postLinkTag=function(tag,arg,txt){
	var x = arg ?  arg.split(',') : txt.split(',') , 
	z = (tag.toLowerCase()=='pid' || (x[1] && x[2])) ,
	txt = arg ? txt : (z ? '回复'+txt : '主题'+txt)
	if(x[1] && x[2]){
		var w = window
		x[1] = w.__NUKE.toInt(x[1])
		x[2] = w.__NUKE.toInt(x[2])
		if(x[0]>x[1]){
			x[4] = x[0]
			x[0] = x[1]
			x[1] = x[4]
			}
		return this.writelink('http://bbs.ngacn.cc/read.php?tid='+x[0]+'&topid='+x[1]+'&page='+x[2]+'#pid'+x[1]+'Anchor', txt)
		}
	else if(z)
		return this.writelink('http://bbs.ngacn.cc/read.php?pid='+x[0], txt)
	else
		return this.writelink('http://bbs.ngacn.cc/read.php?tid='+x[0], txt)
}

ubbcode.dbLinkGen  = function(arg)
{
if(arguments.length>1)
	var str = arguments
else{
	var p = arg.indexOf('[',1), str = arg.substr(1,p-1).replace(/^[,\s]*|[,\s]*$/g,'').split(/\s*,\s*/g)
	str.push(arg.substr(p+1,arg.length-p-1-2).replace(/^\s*|\s*$/g,''))
	}

var t0 = str[0], name=str[str.length-1] , t1='', id=0, lang='cn', x='', u='', h='', key='', std='', raw
str[0] = str[str.length-1] = null
for(var i=0; i<str.length; i++){
	switch(str[i]){
		case 'achieve':
			str[i] = 'achievement';
		case 'achievement':
		case 'item':
		case 'spell':
		case 'quest':
		case 'npc':
		case 'rune':
		case 'skin':
		case 'hero':
			t1 = str[i];
			std +=','+t1;
			break;
		case 'cn':
		case 'tw':
		case 'en':
		case 'us':
			lang = str[i];
			std +=','+lang;
			break;
		case 'raw':
			raw=1
			break;
		default:
			if(str[i] && str[i].toString().match(/^\d+$/)){
				id = str[i];
				std +=','+id;
				}
		}
	}
	
std = '&#91;'+t0+std+'&#91;'+name.replace(/[\x00-\x7f]/g,function($0){return '&#'+$0.charCodeAt(0)+';'})+'&#93;&#93;'

if(!t0)
	t0='wow'

if (name && name.toString().match(/^\d+$/))
	id = name
else
	key = encodeURIComponent(name)

var i = name.match(/\[img\](.+?)\[\/img\]/i);
if(i){
	key = null
	name = "<img src='"+i[1]+"'/>"
	}

if(t0=='wow'){
	if (t1=='achieve')
		t1='achievement'
	if (id && t1){
		h = 'http://db.178.com/'+t0+'/'+lang+'/'+t1+'/'+id+'.html'
		}
	else if (key){
		h = 'http://db.178.com/'+t0+'/'+lang+'/search.html?name='+key
		}
	}
else if(t0=='lol'){
	if(id && t1){
		h = 'http://db.178.com/lol/'+t1+'-'+id
		}
	else if(key){
		h = 'http://db.178.com/lol/search/s/na:'+key+'&item:on&rune:on&hero:on&skin:on'
		}
	}

if(!h)
	return typeof(arg)=='string' ? arg : std

	return "<a class='b' href='"+h+"' onclick='window.jsInterface.openUrl(\""+h+"\");' >"+name+"</a>";
}

ubbcode.getarmory = function(realm,name,o,host,update,todo)
{
	function e(t){return escape(encodeURIComponent(t)).replace(/%25/g,'%')}
	function e2(t){t=t.replace("'",'').replace(" ",'-');return escape(encodeURIComponent(t)).replace(/%25/g,'%')}
	if (host=='cn')
		{
		var hosthost = 'www.battlenet.com.cn'
		var u = 'wow/zh/character/'+e(realm)+'/'+e(name)+'/advanced'
		}
	else if (host=='us')
		{
		var hosthost = 'us.battle.net'
		var u = 'wow/en/character/'+e(realm)+'/'+e(name)+'/advanced'
		}
	else if (host=='eu')
		{
		var hosthost = 'eu.battle.net'
		var u = 'wow/en/character/'+e(realm)+'/'+e(name)+'/advanced'
		}
	else if (host=='tw')
		{
		var hosthost = 'tw.battle.net'
		var u = 'wow/zh/character/'+e(realm)+'/'+e(name)+'/advanced'
		}
		o.innerHTML="[<a class='b' href='http://"+hosthost+"/"+u+"' onclick=\"window.jsInterface.openUrl('http://"+hosthost+"/"+u+"')\" target='_blank'>"+realm+' '+name+"</a>]"
		return;
}//fe

//=============================
//random
//=============================
ubbcode.randomBlock ={
all:[],
show:function(id,x){
if(!this.all[id])return
var r = this.all[id]
if(x){
	for(var i=0;i<r.length;i++)
		$(r[i]).style.display='block'
	}
else
	$(r[Math.floor(Math.random()*r.length)]).style.display='block'
}//fe
}//ce
//=============================
//嵌套引用
//=============================
ubbcode.parseQuote = {
count:false,
parse:function(c){
	this.count = c.indexOf('[quote')+1
	if(this.count)
		c = c.replace(/(?:<br\s*\/?>)?\s*(\[\/?quote\])\s*(?:<br\s*\/?>)?/gi,"$1")
	var limit=0
	while (this.count){
		if(limit++>5)break;
		c = this.exe(c,false)
		}
	return c
	},
exe:function(c,count){
	this.count = count
	var self = this
	return c.replace(/\[quote\](.+?)\[\/quote\]/gi,function($0,$1){
			if($1.match(/\[quote\]/i))
				return '[quote]'+self.exe($1+'[/quote]',true)
			return "<div class='quote'> "+$1+" </div>"
			}
		);
	}
}

//=============================
//图片尺寸调整
//=============================
ubbcode.adjImgSize = function(o){
if(o.src=='about:blank')return
var x=null
if(o.naturalWidth){
	if(o.width<o.naturalWidth || o.hight<o.naturalWidth)
		x=true
	}
else{
	x = new Image()
	x.src = o.src
	if(x.width>o.width || x.height<o.height)
		x=true
	else
		x=null
	}
if(x){
	o.style.border='5px solid darkred'
	o.title+=' 点击放大'
	o.onclick = function(){this.className=this.className.replace('imgmaxwidth',''),this.style.border='none',this.title=this.title.replace(' 点击放大','')}
	}
}//fe


//=============================
//随机数
//=============================
ubbcode.sRand={
seed:2110032,
rnd:function(argsId){
	if (argsId){
		if(!this.seeds[argsId]){
			var arg = ubbcode.bbscodeConvArgsSave[argsId]
			this.seeds[argsId] = arg.authorId+arg.tId+arg.pId
			if(!this.seeds[argsId])
				this.seeds[argsId]=Math.floor(Math.random()*10000)
			}
		this.seeds[argsId] = (this.seeds[argsId]*9301+49297) % 233280;
		return this.seeds[argsId]/(233280.0);
		}
	this.seed = (this.seed*9301+49297) % 233280;
	return this.seed/(233280.0);
	},
rand:function(argsId){
	return this.rnd();
	},
seeds:{}

}

// RC4-based seedrandom version 2.0. Author: David Bau 4/2/2011
// .seedrandom(seed)	set seed
// .seedrandom('yowza', true)	Seeds using the given explicit seed mixed with accumulated entropy.
// .random()	gen
/*
ubbcode.sRand_v2={}
(function(j,i,g,m,k,n,o){function q(b){var e,f,a=this,c=b.length,d=0,h=a.i=a.j=a.m=0;a.S=[];a.c=[];for(c||(b=[c++]);d<g;)a.S[d]=d++;for(d=0;d<g;d++)e=a.S[d],h=h+e+b[d%c]&g-1,f=a.S[h],a.S[d]=f,a.S[h]=e;a.g=function(b){var c=a.S,d=a.i+1&g-1,e=c[d],f=a.j+e&g-1,h=c[f];c[d]=h;c[f]=e;for(var i=c[e+h&g-1];--b;)d=d+1&g-1,e=c[d],f=f+e&g-1,h=c[f],c[d]=h,c[f]=e,i=i*g+c[e+h&g-1];a.i=d;a.j=f;return i};a.g(g)}function p(b,e,f,a,c){f=[];c=typeof b;if(e&&c=="object")for(a in b)if(a.indexOf("S")<5)try{f.push(p(b[a],e-1))}catch(d){}return f.length?f:b+(c!="string"?"\0":"")}function l(b,e,f,a){b+="";for(a=f=0;a<b.length;a++){var c=e,d=a&g-1,h=(f^=e[a&g-1]*19)+b.charCodeAt(a);c[d]=h&g-1}b="";for(a in e)b+=String.fromCharCode(e[a]);return b}i.seedrandom=function(b,e){var f=[],a;b=l(p(e?[b,j]:arguments.length?b:[(new Date).getTime(),j,window],3),f);a=new q(f);l(a.S,j);i.random=function(){for(var c=a.g(m),d=o,b=0;c<k;)c=(c+b)*g,d*=g,b=a.g(1);for(;c>=n;)c/=2,d/=2,b>>>=1;return(c+b)/d};return b};o=Math.pow(g,m);k=Math.pow(2,k);n=k*2;l(Math.random(),j)})([],ubbcode.sRand_v2,256,6,52);
*/

//=============================
//code标签
//=============================

ubbcode.codeTag={
cache:{},
highlighter:{
	lua:"/js_highlighter.lua.js",
	php:"/js_highlighter.php.js",
	c:"/js_highlighter.c.js",
	js:"/js_highlighter.js.js",
	xml:"/js_highlighter.xml.js",
	css:"/js_highlighter.css.js"
	},

loadedHighlighter:{},

parse:function(c){
var self = this
return c.replace(/\[code(=[^\]]+)?\](.+?)\[\/code\]/gi,function($0,$1,$2){
	var s = $2, l=$1 ? $1.substr(1) : 'c' , id = 'code'+Math.floor(Math.random()*1000)
	self.saveCode(id , l, s.replace(/<br\s*\/?>/ig,"\r\n").replace(/&nbsp;/g,' ').replace(/&lt;/g,'<').replace(/&gt;/g,'>').replace(/&quot;/g,'"').replace(/&#39;/g,"'").replace(/&amp;/g,"&"))
	return "<span class='orange'>Code <span class='gray'>("+l+")</span>:</span><br><div class='textfield' id='"+id+"'></div><img src='about:blank' onerror='ubbcode.codeTag.loadCode(this.previousSibling,this.previousSibling.id)' class='x'/><br/><br/>";
	})
},//fe

saveCode:function(id,l,c){
l = this.highlighter[l] ? l : 'c'
this.cache[id]={l:l,c:c}

},//fe

loadCode:function(o,id){
var self = this , parser = function(){
	self.loadedHighlighter[self.cache[id].l]=true
	o.innerHTML=Highlighter.Execute(self.cache[id].c,self.cache[id].l)
	o.style.height='auto'
	}

if (!window.Highlighter)
	loader.script(__COMMONRES_PATH+"/js_highlighter.js",
		function(){
			loader.script(__COMMONRES_PATH+self.highlighter[self.cache[id].l],parser)
			})
else if(!this.loadedHighlighter[this.cache[id].l])
	loader.script(__COMMONRES_PATH+this.highlighter[this.cache[id].l],parser)
else
	parser();

},//fe

save:function (c){

var self = this
c = c.replace(/\[code(=[^\]]+)?\](.+?)\[\/code\]/gi,function($0,$1,$2){
	var s = $2;
	var l = null;
	var h = null;
	var hh = null;
	if ($1)l = $1.substr(1);
	if (self.highlighter[l])
		h=self.highlighter[l]
	else{
		s=s.replace(/\[/g,'&#91;').replace(/\]/g,'&#93;').replace(/ /g,'&nbsp;');
		return ("<br/><br/><span class='orange'>Code:</span><br><div class='textfield'>"+s+"</div><br/><br/>");
		}
	if (typeof(window.Highlighter)=='undefined')
		{
		window.Highlighter={'Brushes':{}};
		commonui.loadScriptInOrder(Array(__COMMONRES_PATH+"/js_highlighter.js"),function(){});
		}
	var id = 'code'+Math.floor(Math.random()*1000);
	s = s.replace(/<br\s*\/?>/ig,"\r\n").replace(/ /g,'&nbsp;').replace(/\[/g,'&#91;').replace(/\]/g,'&#93;');
	s = "<span class='orange'>Code <span class='gray'>("+l+")</span>:</span><br><div class='textfield'></div><textarea id='"+id+"' style='display:none'>"+s+"</textarea><br/><br/>";
	var prase = function(){$(id).previousSibling.innerHTML=Highlighter.Execute($(id).value,l);$(id).previousSibling.style.height='auto'}
	window.setTimeout(function(){commonui.loadScriptInOrder(__COMMONRES_PATH+h,prase)},1000);
	self.cache[id]=s
	return '['+id+']';
	}
	
	);//[code]
return c

},//fe

load:function (c){
var self = this
c = c.replace(/\[(code\d+)\]/gi,function($0,$1){
	if(self.cache[$1])
		return self.cache[$1]
	else
		return $0
	})
return c
}//fe
}
//=============================
//t.178.com
//=============================
ubbcode.bbscode_t178_tpl=function(d,s,uid){
if (uid && !d.user.user_id)
	d.user.user_id=uid
var a = parseInt(d.user.user_id,10).toString(16);
if(a.length<8)
	a = (new Array(8-a.length+1).join('0'))+a
a = d.user.user_id ? "http://pic1.178.com/avatars/"+a.substr(0,2)+"/"+a.substr(2,2)+"/"+a.substr(4,2)+"/50_"+d.user.user_id+".jpg" : window.__IMG_STYLE+"/nobody.gif"
var sn = commonui.cutstrbylen(d.user.nickname,4)
if(sn!=d.user.nickname)sn+='...'

return "<table class='defaultcolor'"+(s?" style='"+s+"'":'')+"><tr><td class='comment1' style='line-height:1.2em;padding-left:2px'>"+
"<nobr><a href='"+d.user.url+"' class='author' target='_blank'><b>"+sn+"</b></a>"+
(parseInt(d.user.vip_type,10) ? "<sup class='silver vip"+d.user.vip_type+"' style='line-height:0.5em'>v</sup>" :'')+
"</nobr><br/><span></span><img src='about:blank' class='x' onerror='this.previousSibling.innerHTML=commonui.loadPostPortrait(\""+a+"\")'/></td><td style='padding:1px;vertical-align:middle'>"+
"<table><tr><td class='b9tl'></td><td class='b9t'></td><td class='b9tr'></td></tr><tr><td class='b9lcc'></td><td class='b9c comment2' style='padding:3px'>"+
"<span class='content' style='line-height:1.5em'>"+
(sn!=d.user.nickname ? "<nobr><a href='"+d.user.url+"' class='author' target='_blank'><b>"+d.user.nickname+"</b></a>"+(parseInt(d.user.vip_type,10) ? "<sup class='silver vip"+d.user.vip_type+"' style='line-height:0.5em'>v</sup>" :'')+"</nobr> : " : '')+
d.content+
(d.resource ? "<br/><span class='resource' style='padding-left:30px;'><a href='"+d.resource.url+"' class='gray b' target='_blank'>"+d.resource.title+"</a></span>" : '')+
(d.source_tweet ? (d.source_tweet.source_tweet ? "<br/><span class='resource' style='padding-left:30px;'><a href='http://t.178.com/t/"+d.source_tweet.id+"' class='gray b' target='_blank'>t.178.com/t/"+d.source_tweet.id+"</a></span>" : this.bbscode_t178_tpl(d.source_tweet)) : '')+
"</span>"+
(d.pic ? "<br/><span class='pic' style='padding-left:30px;'><a href='"+d.pic.large+"' target='_blank'>"+(d.pic.thumb?"<img src='"+d.pic.thumb+"' style='border:1px solid #000'/>":'[图片]')+"</a></span>" :'')+
(d.video ? "<br/><span class='video_t' style='padding-left:30px;'><a href='"+d.video.shorturl+"' title='"+d.video.title+"' target='_blank' class='b'>"+(d.video.thumb?"<img src='"+d.video.thumb+"' style='border:1px solid #000'/>":'[观看视频]')+"</a></span>" :'')+
"<nobr style='font-size:9px;line-height:12px;margin:0 0 -0.5em 0;text-align:right;display:block;-webkit-text-size-adjust: none;'>"+
"<span class='date silver'>"+commonui.time2shortdate(d.create_time)+"</span> "+
 "<span class='view'><a href='"+( d.id ? 'http://t.178.com/t/'+d.id : d.user.url )+"' class='gray b' target='_blank'>"+( d.id ? 't.178.com/t/'+d.id : d.user.url.replace('http://','') )+"</a></span> " +
//((d.source_name && d.source_url.indexOf('http://t.178.com')==-1) ? "<span class='source'><a href='"+d.source_url+"' class='gray' target='_blank'>"+d.source_url.replace(/^http:\/\//i,'').replace(/\/.*$/i,'')+"</a></span> " : '')+
"</nobr></td><td class='b9r'></td></tr><tr><td class='b9bl'></td><td class='b9b'></td><td class='b9br'></td></tr></table>"+
"</td></tr></table>"
}//fe

//=============================
//相册
//=============================
ubbcode.album = {
imgLoad:function(o,src){
var img = new Image
img.onreadystatechange = img.onload= function(){
	if(this && this.readyState && this.readyState!='complete')
		return
	this.onreadystatechange = this.onload=null
	if (this.width>this.height){
		if (this.width>400){
			o.style.height = (this.height/this.width*400)+'px'
			o.style.width='400px'
			}
		}
	else{
		if (this.height>400){
			o.style.width = (this.width/this.height*400)+'px'
			o.style.height='400px'
			}
		}
	o.src=this.src
	o.style.display=''
	o.parentNode.style.height=o.parentNode.style.width='auto'
	}
img.src=src
},//fe
open:function (id){
commonui.userCache.set('ubbcode_album_cache',this.cache[id],86400)
commonui.userCache.save();
},//fe
cache:{},
albumCount:0
}

//=============================
//头条
//=============================


ubbcode.loadHeadLine = function (o,id){
if(!this.loadHeadLineElm || !this.loadHeadLineElm[id])return
var i,j,k,w,ww,td1,td2,tr,sel,col,cls,lt,ltr,clsr,height,tmp,x
height = 250;
if(o.offsetWidth)
	w=o.offsetWidth
else{
	w=o.parentNode
	while(!w.offsetWidth && w!=document.body)w=w.parentNode
	if(w.offsetWidth)
		w=w.offsetWidth
	else
		w=930
	}
if(this.loadHeadLineElm[id]._Img){
	if (w>=1200){
		ww = '750px'
		w='33.3%'
		col=3
		}
	else if (w>=880){
		ww = '500px'
		w='50%'
		col=2
		}
	else{
		ww = '250px'
		w='100%'
		col=1
		}
	}
else{
	if (w>=1200){
		ww = '100%'
		w='20%'
		col=5
		}
	else if (w>=930){
		ww = '100%'
		w='25%'
		col=4
		}
	else{
		ww = '100%'
		w='33.3%'
		col=3
		}
	}
td1 = _$('<td/>')._.css('width',ww)
td2 = _$('/td').$0('style',{height:height+'px'})
sel = _$('<tr/>')
lt = _$('<tbody/>')
clsr='row1'
ltr = _$('<tr/>')._.cls(clsr)
i=j=k=0
for (var k=0;k<this.loadHeadLineElm[id].length;k++)
	{
	x = this.loadHeadLineElm[id][k]
	var swapImg = function (){
			var i,t,u,x
			i= this._.gV('i');t= this._.gV('t');u= this._.gV('u');
			x = this.parentNode._x
			x._.css('backgroundImage','url('+i+')')
			x = this.parentNode._xx
			x.innerHTML = u
			//x=x.childNodes[0]
			//x.style.height=(height-40)+'px'
			//x.style.padding='15px'
			x = this.parentNode.getElementsByTagName('td')
			for (i=0;i<x.length;i++)
				x[i].className='nosel'
			this.className='seled'
			}
	if (x.i){
		
		sel._.aC(_$('<td/>')._.sV({'i':x.i,'u':x.u.replace(/^.+?<\/span> /,'').replace(/<\/a> .+$/,'</a>')})._.on('mouseover',swapImg))
		i++
		}
	else{
		if(cls=='b1')cls='b2'
		else cls='b1'
		if (j==col)
			{
			if (clsr=='row1')clsr='row2'
			else clsr='row1'
			cls='b1'
			j=0
			lt._.aC(ltr)
			ltr = _$('<tr/>')._.cls(clsr)
			}
		tmp =  _$('<td/>')._.css('width',w)._.cls('headlineelm '+cls)
		tmp.innerHTML = x.u
		ltr._.aC( tmp )
		j++
		}
	}
tr = _$('/tr')._.add(
		td1._.add(
			_$('<div/>').$0(
				'className','leftblock',
				'style',{height:height+'px'},
				_$('/table')._.add(
					lt._.add(
						ltr
						)
					)
				)
			)
		)
if(i){
	var xx = _$('/div')._.cls('headlinemask')
	var x  = _$('/div').$0(
		'className','headlineimg',
		xx
		)
	td2._.add(
			_$('/div').$0(
				'className','headlinebg',
				'style',{'width':'100%','height':((height-13)+'px')},
				x
				)
			)
	sel._x = x
	sel._xx = xx
	i=100/i+'%'
	for (var k=0;k<sel.childNodes.length;k++)
		sel.childNodes[k].style.width=i
	td2._.add(
		_$('/table').$0(
			'style',{height:'13px'},
			'className','headlinesel',
			_$('/tbody').$0(
				sel
				)
			)
		)
	tmp = cookieFuncs.getCookie('lastvisit')
	if (!tmp || date.getTime()/1000-tmp>3600*5)
		swapImg.call(sel.childNodes[0])
	else
		swapImg.call(sel.childNodes[Math.floor(sel.childNodes.length*Math.random())])
	tr._.aC(td2)
	}
o.appendChild(_$('<table/>')._.cls('headline')._.aC(_$('<tbody/>')._.aC(tr)))
}//fe





//=============================
//链接生成
//=============================
ubbcode.writelink=function(u,n,h,frame)
{
u=u.replace(/http:\/\/([^\.]+)\.ngacn\.com/i,'http://$1.ngacn.cc')
var c = '';
var a = '';
var s = this.checklink(u);
if (!h){
		if (u != n)
			h = u;
		else
			h = '';
	}
switch (s)
	{
		case -1:
			if (u.indexOf(__BBSURL)!=0){
				if (__BBSURL=='http://bbs.ngacn.cc')
					var tmp=u.replace(/^http:\/\/nga\.178\.com/,__BBSURL)
				else
					var tmp=u.replace(/^http:\/\/bbs\.ngacn\.cc/,'http://nga.178.com')
				if(n==u)
					n=u=tmp
				else
					u=tmp
				}
		case 0:
		case 1:
			c = 'silver';
			break;
		case 2:
			c = 'chocolate';
			break;
		case 3:
			c = 'red';
			break;
	}
//if (h)
//	{
//		h = " onmouseover='this.childNodes[0].style.display=\"block\"' onmouseout='this.childNodes[0].style.display=\"none\"'><span class='urltip "+c+"'>"+h+" </span>";
//	}
//else 
//	{
		h = ">";
//	}
/*if (s>1)
	{
		h = " onclick='this.previousSibling.style.display=\"inline\";return false' "+h;
		a = "<span class='urltip "+c+"' style='font-size:11px;padding:2px;text-align:center;line-height:15px' unselectable=on><div>"+u.replace(/http:\/\/([^\/]+)/i,"http://<b style='color:red'>$1</b>")+"</div><div> 此网页不属于本网站，不保证其安全性 </div><div> <a href='"+u+"' onclick='this.parentNode.parentNode.style.display=\"none\"' target='_blank'>继续访问</a> <a href='javascript:void(0)' onclick='this.parentNode.parentNode.style.display=\"none\"'>取消</a> <a href='"+u+"' "+(window.cookieFuncs ? "onclick='this.parentNode.parentNode.style.display=\"none\";cookieFuncs.setMiscCookieInSecond(\"ngabbsnochecklink\",1,1296000)' target='_blank'>不再提示我</a>" : '')+" </div></span>";
	}*/
return "<span class='apd "+c+"'>[</span>"+a+"<a href='"+u+"' target='_blank' onclick=\"window.jsInterface.openUrl('"+u+"')\""+h+n+"</a><span class='apd "+c+"'>]</span>";
}
//fe

//=============================
//地址检查
//=============================
ubbcode.checklink=function(u,nocookie,tbl)
{
if(!tbl)tbl=this.checkLinkTable
var c = false
if (window.cookieFuncs)c = cookieFuncs.getMiscCookie('ngabbsnochecklink')
if (!c || nocookie)
	{
	if (u.charAt(0)=='/')return 1;
	u = u.toLowerCase().match(/^http:\/\/([^\/]+)/);
	if (u && u[1])
		{
		u = u[1].split('.');
		var x = u[u.length-2]+'.'+u[u.length-1];
		if(tbl[x])
			if (tbl[x]=='add1')
				{
				if (u[u.length-3])
					x=u[u.length-3]+'.'+x
				if(tbl[x])return tbl[x]
				}
			else
				return tbl[x]
		}
	return 2;
	}
else
	return 0;
}
//fe

//=============================
//加解密
//=============================
ubbcode.crypt = {
'key':{},
'genKey':function(k){
var s=[],i=0,j=0,x;
for (; i<256; i++)
	s[i] = i;
for (i=0; i<256; i++) {
	j = (j + s[i] + k.charCodeAt(i % k.length)) % 256;
	x = s[i];
	s[i] = s[j];
	s[j] = x;
	}
this.key[k] = s
},
/* RC4 symmetric cipher encryption/decryption
Copyright (c) 2006 by Ali Farhadi.
released under the terms of the Gnu Public License.

Email: ali[at]farhadi[dot]ir
Website: http://farhadi.ir/ */
'rc4':function (k, pt) {
	if(!this.key[k])
		this.genKey(k);
	var i=0,j=0,y=0,s=[],ct='',x;
	for (; i<256; i++)
		s[i] = this.key[k][i];
	for (i=0; y<pt.length; y++) {
		i = (i + 1) % 256;
		j = (j + s[i]) % 256;
		x = s[i];
		s[i] = s[j];
		s[j] = x;
		ct += String.fromCharCode(pt.charCodeAt(y) ^ s[(s[i] + s[j]) % 256]);
	}
	return ct;
},
'cT':{
	'0':'~','1':'!','2':'#','3':'|','4':'^','5':'(','6':')','7':'=','8':'`','9':']','a':';','b':',','c':'?','d':':','e':'{','f':'}','A':';','B':',','C':'?','D':':','E':'{','F':'}'
	},
'eT':{
	'~':'0','!':'1','#':'2','|':'3','^':'4','(':'5',')':'6','=':'7','`':'8',']':'9',';':'a',',':'b','?':'c',':':'d','{':'e','}':'f'
	},
'c':function(t){
t = escape(t)
var cT =this.cT
t = t.replace(/(?:%u([a-fA-F0-9])([a-fA-F0-9])([a-fA-F0-9])([a-fA-F0-9]))/g,function ($0,$1,$2,$3,$4){
	return cT[$1]+cT[$2]+cT[$3]+cT[$4]
	})
return t
},//fe
'e':function (t){
if (t.match(/[^\*@\-_+\.\/~!#\^()=`\];,\?:\{\}|A-Za-z0-9%]/)){
	window.alert('编码错误')
	return
	}
var eT =this.eT
t=t.replace(/([~!#\^()=`\];,\?:\{\}|])([~!#\^()=`\];,\?:\{\}|])([~!#\^()=`\];,\?:\{\}|])([~!#\^()=`\];,\?:\{\}|])/g,function ($0,$1,$2,$3,$4){
	return '%u'+eT[$1]+eT[$2]+eT[$3]+eT[$4]
	})
return unescape(t)
}//fe
}//ce

ubbcode.decryptCache = {}
ubbcode.decrypt=function (pass,txt,cC,argsId){
if (!pass)
	return window.alert('请输入密码')
var txt = this.crypt.rc4(pass,this.crypt.e(txt))
txt = txt.replace(/\n/g,'<br/>').replace(/\r/g,'').replace(/\[crypt\]/g,'')
cC.style.display='none'
cC.innerHTML = txt
if(argsId && this.bbscodeConvArgsSave[argsId]){
	//var arg = []
	//for (var k in this.bbscodeConvArgsSave[argsId])
	//	arg.push(this.bbscodeConvArgsSave[argsId][k])
	//arg[0]=cC
	this.bbscodeConvArgsSave[argsId].c=cC
	this.bbsCode(this.bbscodeConvArgsSave[argsId]);
	cC.style.display=''
	}
}//fe

//=============================
//nouse
//=============================
ubbcode.sc2reply_draw=function(r,thumbbase,oi,od,oa){
if (!oa)
	return
var h = '地图: <b>'+r.map+'</b><br/>';
if (r.time)
	{
	var ih = Math.floor(r.time/3600)
	var im = Math.floor((r.time-ih*3600)/60)
	var is = Math.floor(r.time-ih*3600-im*60)
	h+='时长: <b>'+ih+':'+im+':'+is+'</b><br/>'
	}
var c ,n;
n='';
for (var i in r.player )
	{
	if(typeof(r.player[i])!='object')continue
	if(!r.player[i]['id']){
		r.player[i]['id']=r.player[i][0]
		r.player[i]['race']=r.player[i][1]
		r.player[i]['color']=r.player[i][2]
		}
	c=''
	if (r.player[i]['color'])
		c = ' style="color:#'+r.player[i]['color']+'"'
	if (r.player[i]['id']){
		h+= '<b'+c+'>'+r.player[i]['id']+'</b> <span class=gray>('+r.player[i]['race']+')</span><br/>'
		n+= '_'+r.player[i]['id']+'_['+r.player[i]['race']+']'
		}
	}
if (r.thumb){
	oi.src=thumbbase+'/'+r.thumb
	oi.onerror=null
	}
else
	oi.style.display='none'
od.innerHTML = h+od.innerHTML
oa.href += '?filename='+r.map+n+'.sc2replay'
}//fe

ubbcode.attach_org_name=function(o,n){
if (o.nodeName!='A'){
	o=o.getElementsByTagName('a')
	if(!o[0])return
	o=o[0]
	}
o.innerHTML = decodeURIComponent(n)
o.href+='?filename='+n
}//fe

//=============================
//附件
//=============================
ubbcode.attach = {
load:function (oo,o,a,pid,tid,authorId,postTime){
if(typeof(oo)=='string')oo = $(oo)
if(typeof(o)=='string')o = $(o)
if (typeof(a)=='string'){
	a = this.cache[a]
	pid = a.pid
	tid = a.tid
	authorId = a.authorid
	postTime = a.postTime
	a=a.a
	}
else{
	var x = true
	if(!window.__SMALL_SCREEN){
		for (var k=0 ;k<a.length;k++){
			if (o.innerHTML.indexOf(a[k].url)==-1){
				x=false
				break
				}
			}
		}
	}

if (x){
	var id = Math.random()+'_'
	this.cache[id] = {a:a,pid:pid,tid:tid,authorId:authorId,postTime:postTime}
	x='<button type=button onclick="ubbcode.attach.load(this.parentNode,\''+o.id+'\',\''+id+'\')">显示附件</button>'
	}
else{
	x=''
	for (var k=0 ;k<a.length;k++)
		x+=this.gen1(tid,pid,a[k].aid,authorId,a[k].url,a[k].name,a[k].type,a[k].thumb,a[k].dscp,a[k].size,a[k].url_utf8_org_name,postTime)
	}
oo.innerHTML=x
},//fe

gen1:function (tid,pid,aid,authorId,url,name,type,thumb,dscp,size,url_utf8_org_name,postTime){

if (url.indexOf('/')==-1)
	url = 'mon_'+commonui.time2date(postTime,'Ym/d')+'/'+url;

var orglink='',main='',del=''

if (__GP['admincheck'] ||  __CURRENT_UID ==authorId)
	del = "[<a href='javascript:void(0)' onclick='__NUKE.doPost({u:\"/nuke.php\",t:\"_blank\",a:{func:\"delattach\",pid:\""+pid+"\",tid:\""+tid+"\",aid:\""+aid+"\"},b:this})'>删除</a>]";

if(type == 'img'){
	if (thumb==1){
		orglink = "[<a target='_blank' href='"+commonui.getAttachBase(url)+"/"+url+"' target='_blank'>查看原图</a>]";
		main = "<a href='"+commonui.getAttachBase(url)+"/"+url+"' target='_blank' title='点击查看原图'><img src='"+commonui.getAttachBase(url)+"/"+url+".thumb.jpg' alt=''/></a>";
		}
	else
		main = "<img src='"+commonui.getAttachBase(url)+"/"+url+"' alt=''/>";
	return "<table class='left quote'><tr><td>"+dscp+"<div class='clear'></div>"+main+"<div class='clear'></div>"+orglink+" "+del+"</td></tr></table>";
	}
else{
	return "<table class='quote'><tr><td>"+dscp+" <a href='"+commonui.getAttachBase(url)+"/"+url+(url_utf8_org_name ? '?filename='+decodeURIComponent(url_utf8_org_name) : '')+"' target='_blank' class='green'>"+(url_utf8_org_name ? decodeURIComponent(url_utf8_org_name) : name)+"</a> ("+size+" K) "+del+"</td></tr></table>";

	}


},//fe

cache:{}

}


//=============================
/*
bbscode提示
0元素为简要说明
1元素为详细说明
2元素为添加向导

添加向导的结构
{
	0:'请输入用户名',//参数1提示
	1:'请输入用户id',//参数2提示
	2:{'hint:'请输入用户组',//参数3提示
		'opts':{
			0:{0:'管理员',//参数3选项0名称
				1:'admin'//参数3选项0值
				},
			1:{0:'用户',//参数3选项1名称
				1:'user'//参数3选项1值
				},
			}//参数3选项
		}
	3:function(v){
		v[0] = '[username]'+v[0]+'[/username]' 参数数组v 参数ID对应提示的id
		v[1] = '[uid]'+v[1]+'[/uid]'
		v[2] = '[group]'+v[2]+'[/group]'
		return '[user]'+v[0]+v[1]+v[2]+'[/user]'
	}//参数数组处理函数 返回生成的bbscode 如返回false则视为参数错误中止
}
*/
ubbcode.codeHelpCommon = [
{0:'<b>[color]</b><br/><nobr>文字颜色</nobr>',1:"<b>文字颜色</b><br/><br/>	选中你希望加颜色的文字并使用下面的选择器选择颜色<br/>"},
{0:'<b>[size]</b><br/><nobr>文字大小</nobr>',1:"<b>文字大小</b><br/><br/>	选中你希望改变尺寸的文字并使用下面的选择器选择尺寸<br/>"},
{0:'<b>[font]</b><br/><nobr>文字字体</nobr>',1:"<b>文字字体</b><br/><br/>	选中你希望改变字体的文字并使用下面的选择器选择字体<br/>"},
{
	0:'<b>[b]</b><br/><nobr>粗体文字</nobr>',
	1:"<b>粗体文字</b><br/><br/>	[b]甲乙丙丁戊己庚辛[/b]<br/>",
	2:{
		0:{'hint':'请输入文字'},
		1:function(v){
			return '[b]'+v[0]+'[/b]'
		}
	}
},
{
	0:'<b>[u]</b><br/><nobr>下划线文字</nobr>',
	1:"<b>下划线文字</b><br/><br/>	[u]甲乙丙丁戊己庚辛[/u]<br/>",
	2:{
		0:{'hint':'请输入文字'},
		1:function(v){
			return '[u]'+v[0]+'[/u]'
		}
	}
},
{
	0:'<b>[i]</b><br/><nobr>斜体文字</nobr>',
	1:"<b>斜体文字</b><br/><br/>	[i]甲乙丙丁戊己庚辛[/i]<br/>",
	2:{
		0:{'hint':'请输入文字'},
		1:function(v){
			return '[i]'+v[0]+'[/i]'
		}
	}
},
{
	0:'<b>[del]</b><br/><nobr>删除线</nobr>',
	1:"<b>有删除线的文字</b><br/><br/>	[del]甲乙丙丁戊己庚辛[/del]<br/>",
	2:{
		0:{'hint':'请输入文字'},
		1:function(v){
			return '[del]'+v[0]+'[/del]'
		}
	}
},
{
	0:'<b>[align]</b><br/><nobr>左/中/右对齐</nobr>',
	1:"<b>左/中/右对齐文字</b><br/><br/>	[align=left]<br/>	甲乙丙丁戊己庚辛<br/>	左对齐文字<br/>	[/align]<br/><br/>	[align=center]<br/>	甲乙丙丁戊己庚辛<br/>	中对齐文字<br/>	[/align]<br/><br/>	[align=right]<br/>	甲乙丙丁戊己庚辛<br/>	右对齐文字<br/>	[/align]<br/>	",
	2:{
		0:{'hint':'请输入文字'},
		1:{
			'hint':'选择对齐方向',
			'opts':{
				0:{0:'左对齐',1:'left'},
				1:{0:'中对齐',1:'center'},
				2:{0:'右对齐',1:'right'}
				}
			},
		2:function(v){
			return '[align='+v[1]+']'+v[0]+'[/align]'
		}
	}
},
{
	0:'<b>[h]</b><br/><nobr>段落标题</nobr>',
	1:"<b>段落标题</b><br/><br/>	[h]甲乙丙丁戊己庚辛[/h]<br/><br/>	或使用三连等号亦可<br/><br/>	===甲乙丙丁戊己庚辛===<br/>	",
	2:{
		0:{'hint':'请输入标题文字'},
		1:function(v){
			return '==='+v[0]+'==='
		}
	}
},
{0:'<b>[l/r]</b><br/><nobr>左/右浮动</nobr>',1:"<b>段落左/右浮动</b><br/><br/>	[l]左浮动段落<br/>	甲乙丙丁戊己庚辛[/l]<br/><br/>	[r]右浮动段落<br/>	甲乙丙丁戊己庚辛[/r]<br/>	"},
{
	0:'<b>[list]</b><br/><nobr>列表条目</nobr>',
	1:"<b>列表条目</b><br/><br/>	[list]<br/>	[*]条目1<br/>	[*]条目2<br/>	[*]条目3<br/>	[*]条目4<br/>	[/list]<br/>	",
	2:{
		0:{'hint':'第1条'},
		1:{'hint':'第2条'},
		2:{'hint':'第3条'},
		3:{'hint':'第4条'},
		4:{'hint':'第5条'},
		5:{'hint':'第6条'},
		6:{'hint':'第7条'},
		7:{'hint':'第8条'},
		8:{'hint':'第9条'},
		9:{'hint':'第10条'},
		10:function(v){
			var x=''
			for (var i=0;i<v.length;i++)
				{
				if (v[i])
					x+='[*]'+v[i]+'\n'
				}
			return '[list]'+x+'[/list]'
		}
	}
},
{
	0:'<b>[img]</b><br/><nobr>插入图片</nobr>',
	1:"<b>插入图片</b><br/><br/>	[img]http://xxx.com/ooo.jpg[/img]<br/>	",
	2:{
		0:{'hint':'请输入图片地址'},
		1:function(v){
			return '[img]'+v[0]+'[/img]'
		}
	}
},
{
	0:'<b>[album]</b><br/><nobr>插入相册</nobr>',
	1:"<b>插入相册</b><br/><br/>	[album=相册标题]<br/>http://xxx.com/ooo.jpg<br/>http://xxx.com/xxx.jpg<br/>http://xxx.com/xoo.jpg<br/>http://xxx.com/oox.jpg<br/>[/album]<br/><br/>每行一张图片<br/>第一张图片将作为封面显示	",
	2:{
		0:{'hint':'请输入相册标题'},
		1:{'hint':'请输入图片地址 每行一个','cols':30,'rows':20},
		2:function(v){
			if(v[0])v[0]='='+v[0]
			else v[0]=''
			return '[album'+v[0]+']\n'+v[1]+'\n[/album]'
		}
	}
},
{
	0:'<b>[url]</b><br/><nobr>插入链接</nobr>',
	1:"<b>插入链接</b><br/><br/>	[url]http://xxx.com[/url]<br/><br/>	[url=http://xxx.com]点此链接[/url]<br/>	",
	2:{
		0:{'hint':'请输入链接地址'},
		1:{'hint':'请输入链接文字(可以不填)'},
		2:function(v){
			if (v[1])
				return '[url='+v[0]+']'+v[1]+'[/url]'
			else
				return '[url]'+v[0]+'[/url]'
		}
	}
},
{
	0:'<b>[quote]</b><br/><nobr>引用文字</nobr>',
	1:"<b>引用文字</b><br/><br/>	[quote]<br/>	引用文字<br/>	甲乙丙丁戊己庚辛<br/>	[/quote]<br/>	",
	2:{
		0:{'hint':'请输入引用文字'},
		1:function(v){
			return '[quote]'+v[0]+'[/quote]'
		}
	}
},
{0:'<b>[code]</b><br/><nobr>程序代码</nobr>',1:"<b>程序代码</b><br/><br/>	[code]\n\
	for (i=0; i<10; i++)\n\
	 &nbsp; {\n\
	 &nbsp; &nbsp; print('hello world')\n\
	 &nbsp; }\n\
	[/code]\n\
\n\
	支持以下语法高亮\n\
	[code=lua] …… [/code] lua\n\
	[code=php] …… [/code] php\n\
	[code=c] …… [/code] c\n\
	[code=js] …… [/code] javascript\n\
	[code=xml] …… [/code] xml/html\n\
"},
{
	0:'<b>[flash]</b><br/><nobr>插入flash(视频)</nobr>',
	1:"<b>插入flash(仅限于youtube.com/tudou.com/youku.com等站点)</b><br/><br/>	[flash]http://xxx.com/ooo.swf[/flash]<br/>",
	2:{
		0:{'hint':'请输入flash地址(仅限于youtube.com/tudou.com/youku.com等站点)'},
		1:function(v){
			return '[flash]'+v[0]+'[/flash]'
		}
	}
},
{0:'<b>[table]</b><br/><nobr>插入表格</nobr>',1:"<b>插入表格</b><br/><br/>[table]\n\
  [tr]\n\
  &nbsp; [td33]第一行第一列，宽度33%[/td]\n\
  &nbsp; [td25]第一行第二列，宽度25%[/td]\n\
  &nbsp; [td]第一行第三列，宽度自动[/td]\n\
  [/tr]\n\
  [tr]\n\
  &nbsp; [td]第二行第一列[/td]\n\
  &nbsp; [td]第二行第二列[/td]\n\
  &nbsp; [td]第二行第三列[/td]\n\
  [/tr]\n\
[/table]\n\
\n\
[table]\n\
  [tr]\n\
  &nbsp; [td]第一行第一列[/td]\n\
  &nbsp; [td]第一行第二列[/td]\n\
  &nbsp; [td]第一行第三列[/td]\n\
  [/tr]\n\
  [tr]\n\
  &nbsp; [td colspan2 width50]二行第一列和第二列合并 宽度50%[/td]\n\
  &nbsp; [td]第二行第三列[/td]\n\
  [/tr]\n\
  [tr]\n\
  &nbsp; [td colspan=3]第三行第一列和第二列第三列合并[/td]\n\
  [/tr]\n\
[/table]\n\
\n\
[table]\n\
  [tr]\n\
  &nbsp; [td rowspan=3]第一行和第二行第三行第一列合并[/td]\n\
  &nbsp; [td]第一行第二列[/td]\n\
  &nbsp; [td]第一行第三列[/td]\n\
  [/tr]\n\
  [tr]\n\
  &nbsp; [td]第二行第二列[/td]\n\
  &nbsp; [td]第二行第三列[/td]\n\
  [/tr]\n\
  [tr]\n\
  &nbsp; [td]第三行第二列[/td]\n\
  &nbsp; [td]第三行第三列[/td]\n\
  [/tr]\n\
[/table]"},
{
	0:'<b>[tid/pid]</b><br/><nobr>主题/回复</nobr>',
	1:"<b>插入到主题/回复的链接</b><br/><br/>	[tid]主题ID[/tid]<br/>	[tid=主题ID]甲乙丙丁[/tid]<br/>	[pid]回复ID[/pid]<br/>	[pid=回复ID]甲乙丙丁[/pid]<br/>	",
	2:{
		0:{'hint':'请输入ID'},
		1:{'hint':'请输入链接文字(可以不填)'},
		2:{
			'hint':'选择类型',
			'opts':{
				0:{0:'主题',1:'tid'},
				1:{0:'回复',1:'pid'}
				}
			},
		3:function(v){
			if (v[1])
				return '['+v[2]+'='+v[0]+']'+v[1]+'[/'+v[2]+']'
			else
				return '['+v[2]+']'+v[0]+'[/'+v[2]+']'
		}
	}
},
{0:'<b>[dice]</b><br/><nobr>投骰子</nobr>',1:"<b>投一个或多个骰子，并计算总和</b><br/>投骰结果使用随机数计算，以帖子ID和发帖人ID做为种子，故引用他人的投骰代码会得到不同结果<br/><br/>[dice]d100[/dice] 投一个100面的骰子 (1~100)<br/>[dice]2d6[/dice] 投两个6面的骰子 (2~12)<br/>[dice]2d4+2d6[/dice] 投两个4面的骰子和两个6面的骰子 (4~20)<br/><br/>骰子面数不能超过1000,一次最多投10个骰子<br/>"},
{
	0:'<b>[crypt]</b><br/><nobr>插入加密的内容</nobr>',
	1:"<b>请使用向导添加</b>",
	2:{
		0:{'hint':'请设置密码<br/><span style="font-weight:normal">(只有使用正确的密码才能浏览加密的内容<br/>发布人和一定权限以上者可以看到密码)</span>'},
		1:{'hint':'请输入要加密的内容','cols':30,'rows':20},
		2:function(v){
			if (v[0].length<5){
				window.alert('请使用更长的密码')
				return false
				}
			if (v[0].match(/[^0-9A-Za-z_]/)){
				window.alert('请使用大小写字母或数字做密码')
				return false
				}
			if(v[1]=='')return false
			if (postfunc && postfunc.addHiddenInfo)
				postfunc.addHiddenInfo('Password: '+v[0])
			return '[crypt]'+ubbcode.crypt.c(ubbcode.crypt.rc4(v[0],v[1]))+'[/crypt]'
		}
	}
},
{
	0:'<b>[collapse]</b><br/><nobr>插入折叠的内容</nobr>',
	1:"<b>折叠的段落只显示提要和展开按钮 浏览者点击按钮后显示内容</b><br/><br/>[collapse=提要]<br/>隐藏的内容……<br/>[/collapse]<br/><br/>[collapse]<br/>没有提要也可以……<br/>[/collapse]<br/>",
	2:{
		0:{'hint':'提要<span style="font-weight:normal">(可不填)</span>'},
		1:{'hint':'请输入要折叠的内容','cols':30,'rows':20},
		2:function(v){
			if (v[0]){
				if (v[0].length>50){
					window.alert('提要过长')
					return false
					}
				v[0]='='+v[0]
				}
			else
				v[0]=''
			if(v[1]=='')return false
			return '[collapse'+v[0]+']'+v[1]+'[/collapse]'
		}
	}
},
{
	0:'<b>[randomblock]</b><br/><nobr>插入随机段落</nobr>',
	1:"<b>一个帖子中所有的随机段落只会显示一个</b><br/><br/>[randomblock]<br/>一段内容……<br/>[/randomblock]<br/><br/>[randomblock]<br/>这两段只会显示其中一个……<br/>[/randomblock]<br/>"

},
{
	0:'<b>[@用户名]</b><br/><nobr>发送提醒</nobr>',
	1:"<b>该用户在访问论坛时会收到一个提醒，让他能注意到这个帖子</b><br/><br/>[@用户名]<br/>"
},
{
	0:'<b>[t.178.com]</b><br/><nobr>引用178尾巴</nobr>',
	1:"<b>引用178尾巴的内容</b><br/><br/>[t.178.com/用户数字id]<br/><br/>引用这个用户的最新一条信息<br/><br/>[t.178.com/#话题#]<br/><br/>引用这个话题的讨论"
}
];