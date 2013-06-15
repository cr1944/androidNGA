//==================================
//基本
//==================================
__NUKE.scEn=function (v,no){
switch (typeof(v)) { 
	case 'string':
		return v.replace(/~/g,'');
	case 'number':
		return v.toString(10);
	case 'boolean':
		return v?1:0
	case 'object':
		if(no)return ''
		var buf=[]
		for (var k in v)
			buf.push(this.scEn(k,1) + '~' + this.scEn(v[k],1));
		return buf.join('~');
	default: 
		return '';
	}
}//fe

__NUKE.scDe=function (s){
s = s.split('~')
if(s.length==1)return s
var v={}
for (var i=0;i<s.length;i+=2)
	v[s[i]]=s[i+1]
return v
}//fe

//==================================
//commonui
//==================================
if (!window.commonui)
	var commonui = {}

commonui._w = window

//debug============
commonui._debug={
data:{},
length:0,
on:function(){
cookieFuncs.setCookieInSecond("debug",1,3600*24)
},
push:function (e){
this.data[this.length++]=e
},
_d:function d(f,c){
if(f && typeof(c)=='undefined'){
	c=f
	f=''
	}
var r =''
for (var k in c){
	if (typeof(c[k])=='object')
		r+=this._d(f+k+'.',c[k]);
	else
		r+=f+k+' = '+c[k]+'\n';
	}
return r
},
gen:function(){
if (typeof(cookieFuncs.cookieCache[cookieFuncs.misccookiename]) != 'object')
	cookieFuncs.extractMiscCookie();
var r=''
r+='---js debug---\n';
r+=this._d('',this.data);
r+='---cookies---\n';
var cc = document.cookie.split(';');
r+=this._d('',cc);
r+='---misccookies---\n';
r+=this._d('',cookieFuncs.cookieCache[cookieFuncs.misccookiename]);
return r
},
display:function (){
document.write(this.gen())
}
}//ce

//浏览器检测====
commonui.UAInit=function (){
var u = navigator.userAgent.toLowerCase()
window.__UA={}
if ((x=u.indexOf('msie'))!=-1){
	x=u.substr(x+5,1)
	__UA[0]=1
	}
else if ((x=u.indexOf('chrome'))!=-1){
	x=u.substr(x+7,2)
	__UA[0]=2
	}
else if (x=u.indexOf('firefox')!=-1){
	x=u.substr(x+7,1)
	__UA[0]=3
	}
__UA[1]=parseInt(x,10)
if(__UA[0]==1 && __UA[1]<=6){
	try{document.execCommand('BackgroundImageCache',false,true)}catch(e){};
	window.isIE6=true;//
	}
if ((x=u.indexOf('windows nt'))!=-1){
	x=u.substr(x+11,2)
	__UA[2]=1
	__UA[3]=parseInt(x,10)
	}
}

//事件注册==================
commonui.aE=function(obj,e,fn) {
if (e=='DOMContentLoaded'){
	var x = this._addEventOnDOMContentLoadedFucns
	x[x.length++]=fn
	if(x.done)fn()
	return
	}
else if (e=='beforeunload' || e=='pagehide')
	e= ('onpagehide' in window) ? 'pagehide' : 'beforeunload'
	
if (obj.addEventListener)
	obj.addEventListener(e,fn,false)
else if (obj.attachEvent)
	obj.attachEvent('on'+e,function(ee){if(!ee)ee=window.event;fn.call(obj,ee)})
}//fe

commonui._addEventOnDOMContentLoadedFucns={length:0}
commonui.triggerEventDOMContentLoadedAct = function(){
var x = this._addEventOnDOMContentLoadedFucns
for (var i=0;i< x.length;i++)
	x[i]()
x.done=true
}//fe

commonui.cancelBubble=function(e){
if (!e) var e = window.event;
e.cancelBubble = true;
if (e.stopPropagation) e.stopPropagation()
}
commonui.cancelEvent=function(e){
if (!e) var e = window.event;
e.returnValue = false
if (e.preventDefault) e.preventDefault()
return false
}

//控制台======
commonui.console = {
cache:null,
ckey:null,
_w:null,
_t:{value:''},
echo:function (txt,nonl){
if(typeof(txt)=='object')
    txt = commonui._debug._d(txt)
if(!nonl)txt+='\n'
this._t.value+=txt
var x = this._t.scrollHeight - this._t.clientHeight;
if (x>0)
	this._t.scrollTop =x
},//fe
open:function (){
if(!this._w){
	var v=this._t.value
	this._t = _$('<textarea/>')._.css({width:'500px',height:'200px',overflow:'hidden',display:'block',background:'#000',border:'none',color:'#eee',margin:'0',padding:'0',fontFamily:'Monospace',textAlign:'left'})._.attr('readonly','readonly')
	this.echo(v)
	this._w = _$('<div/>')._.css({left:'5px',top:'5px',padding:'5px',position:'absolute',display:'none',background:'#000',border:'2px solid #aaa',borderRadius:'8px','boxShadow':'9px 9px 9px #444'})._.aC(this._t,
		_$('<input/>')._.attr('type','text')._.css({width:'480px',background:'#000',border:'none',color:'#eee',fontFamily:'Monospace'})._.on('keyup',function(e){commonui.console.input(e,this)}),
		_$('<button/>')._.attr({type:'button',innerHTML:'&lt;'})._.css({width:'20px'})._.on('click',function(e){commonui.console.input({keyCode:13},this.previousSibling)})
		)
	document.body.appendChild(this._w)
	}
if(this._w.style.display=='none'){
	var s = this.getScroll();
	this._w.style.left=(s.x+5)+'px'
	this._w.style.top=(s.y+5)+'px'
	this._w.style.display='block'
	this._w.getElementsByTagName('input')[0].focus()
	}
else
	this._w.style.display='none'
},//fe
input:function(e,o){
if (!e)e = window.event;
if(e.keyCode == 13 || e.keyCode == 38 || e.keyCode == 40){
	var c = this.cache,k = this.ckey
	if(!c)c=commonui.userCache.get('consoleCache')
	if(!c)c=[]
	if(k==null)k=c.length-1

	if (e.keyCode == 13 && o.value){
		var x = o.value
		o.value=''
		c.push(x)
		if(c.length>20)c.shift()
		k = c.length-1
		this.cache=c
		commonui.userCache.set('consoleCache',c)
		try{
			eval('var r=( '+x+' )')
			}
		catch(e){
			try{
				eval('var r=(function(){\n'+x+'\n})()')
				}
			catch (ee){
				if(ee.stack)
					console.log(ee.stack)
				}
			}
		if(typeof(ee)==='undefined')this.echo(r)
		}
	else if (e.keyCode == 38){
		if (typeof(c[k])!='uindefined'){
			o.value = c[k]
			k--
			if (k<0)
				k=c.length-1
			}
		}
	else if (e.keyCode == 40){
		k++
		if (k>=c.length)
			k=0
		if(typeof(c[k])!='uindefined')o.value = c[k]
		}
	
	this.ckey=k
	}
},
getScroll:function (){
return commonui.getScroll()
},//fe
init:function (){
commonui.aE(document,'keyup',function (e){
	var x = document.activeElement
	if (typeof(x)=='undefined' || (x.nodeName && (x.nodeName=='INPUT' || x.nodeName=='TEXTAREA')))return
	if (!e)e = window.event;
	if (e.keyCode == 192){
		commonui.console.open()
		}
	})

if(window.console){
	//Error.prototype.toStringBak = Error.prototype.toString
	//Error.prototype.toString = function(){var x = this.stack+' ';return this.toStringBak()+'\n| '+x.replace(/\n/g,'\n| ')}
	console.logBak = console.log
	try{
		console.log = function(){
			commonui.console.echo.apply(commonui.console,arguments)
			if(this.logBak){
				if(this.logBak.apply)
					this.logBak.apply(this,arguments)
				else
					this.logBak(arguments[0])
				}
			}
		}
	catch(e){
		commonui.console.echo(e)
		}

	}
else{
	console = {
		log:function(e){commonui.console.echo(e)}
		}
	}	
	
}//fe
}
commonui.console.init();

//以中文显示长度为单位截取字符串================
commonui.cutstrbylen=function(s,l)
{
var j = 0.0;
var c= '';
for (var i=0;i<s.length;i++){
	c = s.charCodeAt(i);
	if (c > 127)
		j = j+1;
	else if ( (c<=122 && c>=97)||(c<=90 && c>=65) )
		j = j+0.65;
	else
		j = j+0.35;
	if (j>=l)
		return (s.substr(0,i+1));
	}
return (s);
}

/**
 *判断在iframe中
 */
if (window.parent!=window.self){
	try{
		window.parent.location.href.replace('http','http')
		commonui.checkIfInIframe = function(){return true}
		}
	catch (e){
		commonui.checkIfInIframe = function(){return false}
		}//script must run after document.body init
	}
else
	commonui.checkIfInIframe = function(){return false}

//时间转日期================
commonui.time2date = function(t,f){
if(!t)return '';
if(!this._time2date_date)this._time2date_date=new Date;
var y=this._time2date_date;
y.setTime(t*1000);
if(!f)f='Y-m-d H:i:s'
var x = function(s){s=String(s);if(s.length<2)s='0'+s;return s}
f = f.replace(/([a-zA-Z])/g,function($0,$1){
	switch ($1)
		{
		case 'Y':
			return y.getFullYear()
		case 'y':
			$1 = String(y.getFullYear())
			return $1.substr($1.length-2)
		case 'm':
			return x(y.getMonth()+1)
		case 'd':
			return x(y.getDate())
		case 'H':
			return x(y.getHours())
		case 'i':
			return x(y.getMinutes())
		case 's':
			return x(y.getSeconds())
		}
	})
return f
}//fe
commonui.time2shortdate=function(t,f){
if(!f)f='y-m-d H:i'
return this.time2date(t,f)
}//fe


//时间转时段================
commonui.time2dis = function(y,f)
{
if(!this.time2dis.now){
	if (window.__NOW)
		this.time2dis.now = __NOW
	else{
		this.time2dis.now = new Date;
		this.time2dis.now = Math.floor(this.time2dis.now.getTime()/1000)
		}
	var z = new Date(this.time2dis.now*1000)
	z.setHours(0,0,0)
	this.time2dis.nowDayStart = Math.floor(z.getTime()/1000)
	z.setDate(1)
	z.setMonth(0)
	this.time2dis.nowYearStart = Math.floor(z.getTime()/1000)
	}

var x = this.time2dis.now-y,z=''

if (x<4500){
	z='分钟前'
	if(x<60)
		x="刚才"
	else if(x<450)
		x=5+z
	else if(x<750)
		x=10+z
	else if(x<1050)
		x=15+z
	else if(x<1350)
		x=20+z
	else if(x<1650)
		x=25+z
	else if(x<2100)
		x=30+z
	else if(x<2700)
		x=40+z
	else if(x<3300)
		x=50+z
	else
		x='1小时前'
	}
else{
	if (y>(this.time2dis.nowDayStart-172800)){
		if (y>this.time2dis.nowDayStart)
			z='今天'
		else if (y>(this.time2dis.nowDayStart-86400))
			z='昨天'
		else
			z='前天'
		z+=' H:i'
		}
	else if(y>this.time2dis.nowYearStart)
		z='m-d H:i'
	else
		z=f?f:'Y-m-d'
	x = this.time2date(y,z)
	}

return x;
}//fe

//获取样式=================
commonui.getStyle=function(o,styleProp){
var y=null
if (o.currentStyle)
	y = o.currentStyle[styleProp.replace(/-([a-z])/g,function($0,$1){return $1.toUpperCase()})];
else if (window.getComputedStyle)
	y = document.defaultView.getComputedStyle(o,null).getPropertyValue(styleProp);
return y;
}//fe

//获取高度====================
commonui.getScroll=function (){
var x = document.documentElement.scrollLeft || document.body.scrollLeft || 0;
var y = document.documentElement.scrollTop || document.body.scrollTop || 0;
return {x:x,y:y}
}//fe


//=============================
//inline tip生成

commonui.genTip={
style:{
backgroundColor:'#fffee1',
border:'1px solid #444',
padding:'0px 2px 1px 2px',
textDecoration:'none',
position:'absolute',
display:'none',
lineHeight:'1.33em',
borderRadius:'3px'
},
add:function (o,oo,arg){//arg.triggerElm 触发node  arg.hide=1 移出触发元素既消失
if(typeof(o)=='string')
	o=$(o)
var t = document.createElement('span')
	t.name='tip'
if (typeof(oo)=='string')
	t.innerHTML = oo
else
	t.appendChild(oo)
for (var k in this.style)
	t.style[k]=this.style[k]
if (arg && arg.margin)
	t.style.marginTop = typeof(arg.margin)=='number'? arg.margin+'px' : arg.margin
else
	t.style.marginTop = '-'+(o.offsetHeight-1)+'px'
o.parentNode.insertBefore(t,o)
if(arg && arg.triggerElm)o=arg.triggerElm
o._tip = t
t._parent = o
o.onmouseover = function(){this._tip.style.display='inline'}
if(arg && arg.hide==1){
	o.onmouseout = function(e){
		if (commonui.genTip.checkTo(e,this))
			this._tip.style.display='none'
		}
	}
else{
	o.onmouseout = function(e){
		if (commonui.genTip.checkTo(e,this,this._tip))
			this._tip.style.display='none'
		}
	t.onmouseout = function(e){
		if (commonui.genTip.checkTo(e,this,this._parent))
			this.style.display='none'
		}
	}
},//fe
checkTo:function (e,o,oo){
if (!e) var e = window.event;
var to = e.relatedTarget || e.toElement;
if (to && to != o && to.parentNode != o && to.parentNode.parentNode != o && to.parentNode.parentNode.parentNode != o){
	if (oo){
		if(to != oo && to.parentNode != oo && to.parentNode.parentNode != oo && to.parentNode.parentNode.parentNode != oo)
			return to
		}
	else
		return to
	}
}//fe

}//ce

//=============================
//本地缓存=====================
commonui.userCache ={
cache:null,
change:false,
key:'userCache_',

init:function (){
if(this.cache)return true
if(!window.domStorageFuncs)return false
this.key = 'userCache_'
this.key += window.__CURRENT_UID ? __CURRENT_UID+'_' :'0_'
this.cache = {}
commonui.aE(window,'beforeunload',function(){
	commonui.userCache.save()
	})
return true
},//fe

set:function (k,v,t){
if (!this.init())return false
if (!t)t=86400*30
this.cache[k]={v:v,t:t}
this.change=true
},//fe

get:function (k){
if (!this.init())return false
if(typeof(this.cache[k])=='undefined'){
	var v = window.domStorageFuncs.get(this.key+k)
	if(v!=null)v=cookieFuncs.json_decode(v)
	this.cache[k]={v:v}
	}
return this.cache[k].v
},//fe

del:function (k){
if (!this.init())return false
this.cache[k]={v:null,t:-1}
this.change=true
},//fe

save:function (){
if (!this.init())return false
if (this.change){
	for (var k in this.cache){
		var t = this.cache[k].t
		if(t>0)
			domStorageFuncs.set(this.key+k,cookieFuncs.json_encode(this.cache[k].v),t)
		else if (t<0)
			domStorageFuncs.remove(this.key+k)
		}
	}
this.change=false
}//fe

}//ce

//=============================
//颜色转换=====================
commonui.rgbToHsv=function(r, g, b){//0-255
r = r/255, g = g/255, b = b/255;
var max = Math.max(r, g, b), min = Math.min(r, g, b);
var h, s, v = max;

var d = max - min;
s = max == 0 ? 0 : d / max;

if(max == min){
	h = 0; // achromatic
}else{
	switch(max){
		case r: h = (g - b) / d + (g < b ? 6 : 0); break;
		case g: h = (b - r) / d + 2; break;
		case b: h = (r - g) / d + 4; break;
	}
	h /= 6;
}

return [h, s, v];//0~1
}//fe

commonui.hsvToRgb=function(h, s, v){//0~1
var r, g, b;

var i = Math.floor(h * 6);
var f = h * 6 - i;
var p = v * (1 - s);
var q = v * (1 - f * s);
var t = v * (1 - (1 - f) * s);

switch(i % 6){
	case 0: r = v, g = t, b = p; break;
	case 1: r = q, g = v, b = p; break;
	case 2: r = p, g = v, b = t; break;
	case 3: r = p, g = q, b = v; break;
	case 4: r = t, g = p, b = v; break;
	case 5: r = v, g = p, b = q; break;
}

return [Math.round(r * 255), Math.round(g * 255), Math.round(b * 255)];//0-255
}//fe

commonui.hexToRgb=function (h){
if (h.length>4){
	h = h.match(/[0-9a-f]{2}/ig)
	return [ ("0x"+h[0])- 0, ("0x"+h[1])- 0, ("0x"+h[2])- 0]
	}
h = h.match(/[0-9a-f]/ig)
return [ ("0x"+h[0]+h[0])- 0, ("0x"+h[1]+h[1])- 0, ("0x"+h[2]+h[2])- 0]
}//fe

commonui.rgbToHex=function (rgb){
rgb = rgb.match(/^rgba?\s*\(\s*(\d+)\s*,\s*(\d+)\s*,\s*(\d+)/);
if(rgb){
	for (var h='#',i=1; i<4; i++)
		h+=("0" + parseInt(rgb[i],10).toString(16)).slice(-2)
	return h
	}
else
	return ''
}
//==================================
//弹窗界面
//==================================

commonui.createCommmonWindow = function (){
var y = _$('<div/>')._.cls('div3'),

t = _$('<div/>')._.cls('tip_title tip_title_none')._.aC(
	_$('<a/>')._.cls('close_btn_v2')._.attr({href:'javascript:void(0)',innerHTML:'X'})._.on('click',function(){this.parentNode.parentNode.parentNode.style.display='none'}),
	_$('<span/>')._.cls('title')._.attr('innerHTML','&nbsp;')
	),

w = _$('<div/>')._.cls('single_ttip2')._.aC(
		_$('<div/>')._.cls('div1')._.aC(
			t,
			_$('<div/>')._.cls('div2')._.aC(
				y
				)
			)
		)
			
w._.__c = y
w._.__t = t
w._.addTitle=function(t){
	var x = this.self._.__t
	if(t)
		x.className='tip_title'
	else
		x.className='tip_title tip_title_none'

	x.lastChild.innerHTML = t
	return this.self
	}//fe
w._.addAfterContent=function(o){
	this.self._.__c.parentNode.parentNode.appendChild(o)
	}//fe
w._.addBeforeContent=function(o){
	this.self._.__c.parentNode.parentNode.insertBefore(o,this.self._.__c)
	}//fe
w._.addContent=function (x){
	var z = this.self._.__c
	if(arguments.length>1){
		if(!arguments[0])
			z.innerHTML = ''
		z._.add.apply(z._,arguments)
		return this.self
		}
	if (!x)
		z.innerHTML = ''
		
	else if (typeof(x)=='object')
		z.appendChild(x)
	else
		z.innerHTML+=x
	return this.self
	}//fe
w._.show=function (x,y){
	if(!this.self.parentNode || this.self.parentNode.nodeType!=1)document.body.appendChild(this.self)
	if(x!==undefined && y!==undefined){
		this.self.style.left = x+'px'
		this.self.style.top = y+'px'
		this.self.style.display='block';
		return this.self
		}
	var s = __NUKE.getDocSize()
	if(this.self.style.display=='none')
		this.self.style.visibility='hidden';
	this.self.style.display='block';
	var l = s.sL+(s.cW-this.self.offsetWidth)/2
	var t = (this.self.offsetHeight<s.cH) ? s.sT+(s.cH-this.self.offsetHeight)/2 : s.sT+s.cH/5
	this.self.style.left = Math.floor(l)+'px'
	this.self.style.top = Math.floor(t)+'px'
	this.self.style.visibility='';
	return this.self
	}//fe
w._.hide=function (e){
	this.self.style.display='none';
	return this.self
	}//fe
return w
}//fe



//管理界面窗口================
commonui.createadminwindow = function(id){
if(this.adminwindow)return
this.adminwindow = this.createCommmonWindow()
if(!id)
	this.adminwindow.id = 'commonuiwindow';
else
	this.adminwindow.id = id;
document.body.appendChild(this.adminwindow);
return this.adminwindow
}//fe
commonui.hideAdminWindow = function(){
this.adminwindow.style.display='none'
return this.adminwindow
}//fe

commonui.unselectCheckBox = function(){
var x = document.getElementsByTagName('input')
for(var i=0;i<x.length;i++)
	{
	if(x[i].checked)
		x[i].checked = false
	}
}



commonui.massAdmin ={
getChecked:function (){
var i = ''
for (var k in this){
	if (parseInt(k,10)){
		i+=','+k
		this[k].checked=false
		delete this[k]
		this.length--
		}
	}
if (!i)
	return window.alert('你至少要选择一个')
i= i.substr(1)

return i
},//fe
check:function(o,id){
if (this[id]){
	delete this[id]
	this.length--;
	}
else
	{
	if (this.length==15){
		o.checked=false
		return window.alert('你不能选择更多了')
		}
	this[id]=o
	this.length++
	}
},//fe
length:0
}
//fav==================


commonui.favor = function (e,tid){
this.createadminwindow()
this.adminwindow._.addContent(null)
this.adminwindow._.addContent(
	_$('<button/>')._.attr({innerHTML:'收藏',type:'button'})._.on('click',function(){
			__NUKE.doPost({u:"/nuke.php",
					t:this.nextSibling,
					b:this,
					a:{func:"topicfavor",action:"add",raw:"1",tid:tid}
				})
			}
		)
	)
this.adminwindow._.addContent(_$('<div/>'))
tTip.showdscp(e,this.adminwindow);

}//fe


//admin pass==================
commonui.adminPassInput = function (e){
if(!window.__CURRENT_UID || !window.__NOW)
	return alert('需要先登陆')
this.createadminwindow()
this.adminwindow._.addContent(null)
this.adminwindow._.addContent(
	'某些功能在设置正确的密码后方可使用(有效期1.5天左右) 不同域名需要设置多次 ip变动需要设置多次',
	_$('/br'),
	_$('/input')._.attr('size',20),
	_$('/button')._.attr({innerHTML:'确定',type:'button'})._.on('click',function(){
			var p = this.previousSibling.value.replace(/^\s*|\s*$/g,''), o = this
			loader.script(window.__SCRIPTS.md5,function(){
				__NUKE.doRequest({
					u:'/nuke.php?__lib=lib_admin_code',
					b:o,
					f:function(d){
						if(!d || d.error || !d.n || !d.a || !d.t )
							return alert((d && d.error)? d.error : 'error')
						cookieFuncs.setCookieInSecond('admin_code_'+d.a, hex_md5(p+'_j67h8i'+__CURRENT_UID+''+d.a+''+Math.floor(d.n/d.t)) ,d.t+100)
						alert('done')
						}
					})
				});
			}
		)
	)
tTip.showdscp(e,this.adminwindow);
}//fe
//mass lock==================


commonui.lockmass = function (e)
{
var d = "<option value=''>立刻</option>"
for (var i=0.5;i<24;i+=0.5)
	d+="<option value='"+(i*3600)+"'>"+i+"小时后</option>"
this.createadminwindow()
this.adminwindow._.addContent(null)
this.adminwindow._.addContent( "\
	<form action='nuke.php?func=locktopic' target='commonuiwindowiframe' method='post' onsubmit='commonui.disableInputOnSubmit(this)'><table>\
		<tr>\
			<td>\
				锁定\
			</td>\
			<td>\
				<input name='lock' type='radio' value='0' checked>解除锁定<br/>\
				<input name='lock' type='radio' value='-1' checked>解除锁定和隐藏<br/>\
				<input name='lock' type='radio' value='1' >锁定<br/>\
				<input name='lock' type='radio' value='2' >锁定并隐藏内容<br/>\
			</td>\
		</tr>\
		<tr>\
			<td>\
				<b>延时锁定</b>\
			</td>\
			<td>\
				<select name='delay'>"+d+"</select>锁定\
			</td>\
		</tr>\
		<tr>\
			<td>\
				PM\
			</td>\
			<td>\
				<input type='radio' name='pm' value='1'>是\
				<input type='radio' name='pm' value='0' checked='checked'>否\
			</td>\
		</tr>\
		<tr>\
			<td>\
				PM\
			</td>\
			<td>\
				<textarea name='info' rows='3' cols='20'></textarea>\
			</td>\
		</tr>\
		<tr>\
			<td colspan=2>\
				<input value='' type='hidden' name='tidarray'><button type='button' onclick='if(this.previousSibling.value=commonui.massAdmin.getChecked())this.previousSibling.form.submit()'>锁定</button> <button type='button' onclick='commonui.hideAdminWindow()'>关闭</button>\
			</td>\
		</tr>\
	</table></form>\
	<iframe name='commonuiwindowiframe' id='commonuiwindowiframe' onreadystatechange='commonui.remuseInputAfterSubmit(this)' scrolling='no' allowtransparency='true' src='about:blank' frameBorder='0' style='height:50px;width:200px;border:none;overflow:hidden'></iframe>\
")
tTip.showdscp(e,this.adminwindow);

}//fe


//lesser nuke==================
commonui.lessernuke = function (e,tid,pid,l,lou){
if(!pid)pid=0
this.createadminwindow()
this.adminwindow._.addContent(null)
this.adminwindow._.addContent( "\
<form action='nuke.php' target='commonuiwindowiframe' method='post' onsubmit='commonui.disableInputOnSubmit(this)'><table>\
	<tr>\
		<td>\
			<input type='radio' name='level' value='1'/>禁言2天\
			<input type='radio' name='level' value='2'/>禁言2天 扣1威望<br/>\
			<input type='radio' name='level' value='3'/>禁言6天\
			<input type='radio' name='level' value='4'/>禁言6天 扣2威望<br/>\
			<input tyep='text' name='info' size='23' maxlength='50'/><br/>发送给用户的理由(可以不填)<br/><br/>\
			<input name='submit' value='提交' type='submit'> <input value='关闭' type='button' onclick='commonui.hideAdminWindow()'>\
		</td>\
	</tr>\
</table>\
<input type='hidden' value='"+tid+"' name='tid'/>\
<input type='hidden' value='"+pid+"' name='rid'/>\
<input type='hidden' value='lessernuke' name='func'/>\
<iframe name='commonuiwindowiframe' id='commonuiwindowiframe' onreadystatechange='commonui.remuseInputAfterSubmit(this)' scrolling='no' allowtransparency='true' src='about:blank' frameBorder='0' style='height:50px;width:200px;border:none;overflow:hidden'></iframe>\
</form>\
")
tTip.showdscp(e,this.adminwindow);
}//fe

//删除收藏=================
commonui.favordelmass = function (e){
this.createadminwindow()
this.adminwindow._.addContent(null)
this.adminwindow._.addContent("\
	<form action='nuke.php?func=topicfavor&action=del' target='commonuiwindowiframe' method='post' onsubmit='commonui.disableInputOnSubmit(this)'><table>\
		<tr>\
			<td colspan=2>\
				<input value='' type='hidden' name='tidarray'><button type='button' onclick='if(this.previousSibling.value=commonui.massAdmin.getChecked())this.previousSibling.form.submit()'>移除</button> <button type='button' onclick='commonui.hideAdminWindow()'>取消</button>\
			</td>\
		</tr>\
	</table></form>\
	<iframe name='commonuiwindowiframe' id='commonuiwindowiframe' scrolling='no' allowtransparency='true' src='about:blank' style='height:50px;width:200px;border:none;overflow:hidden'></iframe>\
")
tTip.showdscp(e,this.adminwindow);
}//fe

//举报=================
commonui.logpost = function (e,tid,pid){
this.createadminwindow()
this.adminwindow._.addContent(null)
this.adminwindow._.addContent("\
<form action='nuke.php?func=logpost&log&tid="+tid+"&pid="+pid+"' target='commonuiwindowiframe' method='post'>\
<button type='submit' onclick='this.disabled=true'>举报至版主</button> <button type='button' onclick='commonui.hideAdminWindow()'>取消</button>\
</form>\
<iframe name='commonuiwindowiframe' id='commonuiwindowiframe' scrolling='no' allowtransparency='true' src='about:blank' style='height:50px;width:200px;border:none;overflow:hidden'></iframe>\
")
tTip.showdscp(e,this.adminwindow);
}//fe

//添加评论=================
commonui.comment = function (e,tid,pid)
{
this.createadminwindow()
this.adminwindow._.addContent(null)
this.adminwindow._.addContent( "\
<form action='nuke.php?func=comment' target='commonuiwindowiframe' method='post' onsubmit='commonui.disableInputOnSubmit(this)'><table>\
		<tr>\
			<td>\
				评论<br/>\
				1 不超过300字，图片代码无效<br/>\
				2 评论算一条回复<br/>\
				3 评论页尾的帖子会被挤下去\
			</td>\
		</tr>\
		<tr>\
			<td>\
				<textarea name='info' rows='3' cols='25'></textarea>\
			</td>\
		</tr>\
		<tr>\
			<td>\
				<input name='submit' value='提交' type='submit'> <input value='关闭' type='button' onclick='commonui.hideAdminWindow()'>\
			</td>\
		</tr>\
	</table>\
	<input type='hidden' value='"+tid+"' name='tid'/>\
	<input type='hidden' value='"+pid+"' name='pid'/>\
	<iframe name='commonuiwindowiframe' id='commonuiwindowiframe' onreadystatechange='commonui.remuseInputAfterSubmit(this)' scrolling='no' allowtransparency='true' src='about:blank' frameBorder='0' style='height:50px;width:200px;border:none;overflow:hidden'></iframe>\
	</form>\
")
tTip.showdscp(e,this.adminwindow);
}//fe

//审核=================
commonui.audit = function (e,tid,pid)
{
this.createadminwindow()
this.adminwindow._.addContent(null)
this.adminwindow._.addContent( "\
	<form action='nuke.php?func=audit&yes=0,"+tid+","+pid+"' target='commonuiwindowiframe' method='post' onsubmit='commonui.disableInputOnSubmit(this)'>\
	<input name='submit' value='审核通过' type='submit'> <input value='取消' type='button' onclick='commonui.hideAdminWindow()'>\
	</form>\
	<iframe name='commonuiwindowiframe' id='commonuiwindowiframe' scrolling='no' allowtransparency='true' src='about:blank' style='height:50px;width:200px;border:none;overflow:hidden'></iframe>\
")
tTip.showdscp(e,this.adminwindow);
}
//fe

//评分=================
commonui.addpoint = function (e,tid,pid,fid)
{
this.createadminwindow()
this.adminwindow._.addContent(null)
this.adminwindow._.addContent( "\
	<form action='nuke.php?func=addpoint&tid="+tid+"&pid="+pid+"' target='commonuiwindowiframe' method='post' onsubmit='commonui.disableInputOnSubmit(this)'>\
	增加/扣除用户在本版的声望\
	<table>\
		<tr>\
			<td>\
				声望\
			</td>\
			<td>\
				<input type='text' size='10' maxlength='10' name='rcvc' value=''>(-10000~10000)\
			</td>\
		</tr>\
		<tr>\
			<td>\
				理由\
			</td>\
			<td>\
				<input type='text' size='10' maxlength='16' name='info' value=''>\
			</td>\
		</tr>\
		<tr>\
			<td>\
				PM\
			</td>\
			<td>\
				<input type='radio' name='pm' value='1' checked='checked'>是\
				<input type='radio' name='pm' value='0'>否\
			</td>\
		</tr>\
		<tr>\
			<td colspan=2>\
				<input name='submit' value='提交' type='submit'> <input value='取消' type='button' onclick='commonui.hideAdminWindow()'>\
			</td>\
		</tr>\
	</table>\
	</form>\
	<iframe name='commonuiwindowiframe' id='commonuiwindowiframe' scrolling='no' allowtransparency='true' src='about:blank' style='height:50px;width:200px;border:none;overflow:hidden'></iframe>\
")
tTip.showdscp(e,this.adminwindow);
}
//fe

//引用主题=================
commonui.quotetopic = function (e,tid)
{
this.createadminwindow()
this.adminwindow._.addContent(null)
this.adminwindow._.addContent( "\
	<form action='' target='_blank' method='post'>\
	<table>\
		<tr>\
			<td>\
				在所选版面创建镜像\
			</td>\
			<td>\
				<span>\
					<select onclick='onloadforumlist(this)' name='fid'>\
						<option value='' selected='selected'>选择版面</option>\
					</select>\
				</span>\
			</td>\
		</tr>\
		<tr>\
			<td colspan=2>\
				<input name='submit' value='提交' type='submit'> <input value='关闭' type='button' onclick='commonui.hideAdminWindow()'>\
			</td>\
		</tr>\
	</table>\
	</form>\
	<iframe name='adminwindowiframe' id='adminwindowiframe' scrolling='no' allowtransparency='true' src='about:blank' style='height:50px;width:200px;border:none;overflow:hidden'></iframe>\
</div></div></div>\
")
this.adminwindow.getElementsByTagName('form')[0].action = 'nuke.php?func=quotetopic&tid='+tid;
this.adminwindow.getElementsByTagName('form')[0].target = 'adminwindowiframe';
tTip.showdscp(e,this.adminwindow);
}//fe

//删除回复=================
commonui.delReplay = function (e,tid,pid)
{
this.createadminwindow()
this.adminwindow._.addContent(null)
this.adminwindow._.addContent( "\
<form action='nuke.php?func=delsinglereply&tid="+tid+"&rid="+pid+"' target='commonuiwindowiframe' method='post' style='display:none'></form><button type='button' onclick='this.previousSibling.submit();this.disabled=1' title='如有附件先删除附件'>删除此回复</button> <button type='button' onclick='$(\"massreplyadmin\").submit();commonui.unselectCheckBox();this.disabled=1' title='如有附件先删除附件'>删除选中的回复</button> <button type='button' onclick='commonui.hideAdminWindow()'>取消</button><br/>\
<iframe name='commonuiwindowiframe' id='commonuiwindowiframe' onreadystatechange='commonui.remuseInputAfterSubmit(this)' scrolling='no' allowtransparency='true' src='about:blank' frameBorder='0' style='height:50px;width:200px;border:none;overflow:hidden'></iframe>\
")
$("massreplyadmin").target = 'commonuiwindowiframe';
tTip.showdscp(e,this.adminwindow);

}//fe

//帖子显示 发布者信息显示 签名显示=========

/*
 *内容签名处理生成用户信息综合
 *@param cC 内容node/node id
 *@param cSig 签名node/node id
 *@param cPoster 发帖人信息node/node id
 *@param pos 楼层
 *@param rmd 帖子的推荐值
 *@param cLen 内容长度
 *@param lesser 是否lesser权限
 *@param rvrc 威望
 *@param tid 主题id
 *@param pid 回复id(不是回复为0
 *@param aid 用户id
 *@param avatar 头像
 *@param honor 头衔
 *@param int/ regdate 注册时间
 *@param int/ lastlogin 登录时间
 *@param ip ip地址
 *@param level 用户组 
 *@param mute禁言结束时间
 *@param obj/ medal 徽章
 *@param postNum 发帖数
 *@param fid 当前fid
 *@param money 用户铜币
 *@param site 用户的个人版名
 *@param ifRemark 是否有备注
 *@param obj/ repu 用户声望
 *@param client 用户发帖使用的客户端类型
 *@param cS 标题node/node id
 *@param int/ type 帖子的类型bit
 */
//commonui.postDisp = function (cC,cSig,cPoster,pos,rmd,cLen,tid,pid,aid,ip,fid,client,cS,type){
commonui.postDisp = function (cC,cSig,cPoster,pos,rmd,cLen,lesser,rvrc,tid,pid,aid,avatar,honor,regdate,lastlogin,ip,level,mute,medal,postNum,fid,money,site,ifRemark,repu,client,cS,type,cA){
cC = $(cC)
if (!cC)
	return;

if(type){

	cS = $(cS)
	if ((type & 1024)!==0)
		cS.innerHTML+="<span class='red nobr' title='无法编辑/回复'>[锁定]</span>";
	if ((type & 2)!==0)
		cS.innerHTML+="<span class='red nobr' title='只有作者/版主可见'>[隐藏]</span>";

	if(pid!=0 &&(type & 1026)===1026 && !(window.__GP && __GP['admincheck'])){
		var x = cC.parentNode
		while (x.nodeName!=='BODY' && x.nodeName!=='TABLE')
			x = x.parentNode
		if(x.className=='forumbox postbox')
			x.style.display='none'
		}

	if ((type & 1)!==0)//comment
		return ubbcode.bbscode(cC,1,lesser,rvrc,0,tid,pid,aid)
	}

//var cP = $(cPoster)

if(!cLen){
	//cLen = cC.innerHTML.replace(/<div.+?<\/div>/gim,'').replace(/<a.+?<\/a>/gi,'').replace(/<object.+?<\/object>/gim,'').replace(/<[^>]+>/gi,'');
	cLen = cC.innerHTML.length;
	}

//if(commonui.userInfo.users[aid].buffs[99]){
//	if(cP.parentNode.tagName=='TD')cP.parentNode.className+=' buff_avatar_bg'
//	cC.innerHTML+=' [color=gray]……咩~[/color]'
//	}

if (cC.innerHTML && cC.innerHTML.substr(0,24).indexOf('lessernuke')>-1)
	cLen=0;
if (rmd && rmd<-3){
	rmd=Math.floor(100+rmd*10/1.5);
	cC.style.opacity = '0.'+rmd;
	cC.style.MozOpacity = '0.'+rmd;
	//cC.style.filter = 'progid:DXImageTransform.Microsoft.Alpha(opacity='+rmd+')';
	cLen = 0
	}
ubbcode.bbscode(cC,0,lesser,rvrc,0,tid,pid,aid)

var lite=window.__SMALL_SCREEN?true:false/*, posterinfo =  this.userInfo.genPosterInfo(lite, pos, ip, aid, fid , cLen) 
//commonui.dispUserInfo(lite,pos,avatar,honor,regdate,lastlogin,lesser,ip,level,mute,medal,postNum,rvrc,repu,aid,fid,money,site,ifRemark,$('postauthor'+pos).innerHTML)
cC.parentNode.className+=' pc'+this.userInfo.users[aid].memberid

cP.innerHTML = ''
if(lite){
	var xx = _$('<div/>')
	xx.innerHTML=posterinfo
	xx.className=cP.parentNode.className+' posterInfoLine'
	cC.parentNode.parentNode.insertBefore(xx,cC.parentNode.parentNode.getElementsByTagName('div')[1])
	cP.parentNode.style.display='none'
	}
else{
	cP.innerHTML = posterinfo
	if(client){
		switch (client){
			case 1:
				cP.parentNode.insertBefore(_$("<a/>")._.attr({href:'http://itunes.apple.com/app/i-ze-la-si/id523082300',title:'发送自 Life Style 客户端'})._.cls('client_icon'),cP.parentNode.firstChild);
				break;
			}
		}

	}

commonui.usernamelink($('postauthor'+pos),aid, pos % 20 )//修改链接
*/
cA = $(cA)
if(cA){
	var alterlen = cA.innerHTML.length;
	if(alterlen>0) {
		if(cA.parentNode.style.display=='none')
			cA.parentNode.style.display=''
	  cA.innerHTML=commonui.loadAlertInfo(cA.innerHTML)
	}else{
		cA.parentNode.style.display='none'
	}
}
cSig = $(cSig)
if(cSig && cLen>30){
	if(cSig.innerHTML){
		if(cSig.parentNode.style.display=='none')
			cSig.parentNode.style.display=''
		commonui.loadPostSign(cSig,0,lesser,rvrc,1,tid,pid,aid,lite,cSig.innerHTML)
		}
	}
else{
	if(cSig)
		cSig.parentNode.style.display='none'
	cC.innerHTML+='<br/><br/>'
	}

/*
if(commonui.quoteTo && cC.parentNode.id.substr(0,21)=='postcontentandsubject')
	commonui.aE(cC.parentNode,'mouseup',function(e){commonui.quoteTo.onmouseup(e, parseInt(this.id.substr(21),10) )})
*/
}//fe

commonui.loadAlertInfo=function(info)
{
if(!info)return;
info = info.split(/\t|\n/);
var e = '';
var p = '';
var result = '';
for (var k in info){
	if(typeof(info[k])!='string')continue
	info[k] = info[k].replace(/^[\t\n ]+/,'');
	if (info[k])
		{
		if (info[k].substr(0,4).toLowerCase()=='edit')e+=info[k]+' ';
		else p+=info[k].replace(/\[([\d\.]+) ([\d\.]+) ([\d\.]+)\]/,'[$1声望 $2威望 $3G]')+' ';
		}
	}
if(e)result +=('<div class="silver">'+e+'</div>');
if(p)result +=("<table class='quote'><tr><td>评分记录 "+p+'</td></tr></table>');
return result;
}

//发帖人信息生成
commonui.userInfo = {
self:this,
u:{uid:0,username:'',credit:0,medal:'',reputation:'',groupid:0,memberid:0,avatar:'',yz:0,site:'',honor:'',regdate:0,mute_time:0,postnum:0,rvrc:0,money:0,thisvisit:0,signature:'',nickname:'',buffs:{},bit_data:0},
users:{},
groups:null,
medals:null,
buffs:null,
reputations:null,
setAll:function(v){
if(v.__REPUTATIONS){
	this.reputations = v.__REPUTATIONS
	delete v.__REPUTATIONS
	}
if(v.__GROUPS){
	this.groups = v.__GROUPS
	delete v.__GROUPS
	}
if(v.__MEDALS){
	this.medals = v.__MEDALS
	delete v.__MEDALS
	}
var n=function(u){
	if(u.buffs){
		var now = window.__NOW
		for(var k in u.buffs){
			if(u.buffs[k][1]<now)
				u.buffs[k]=null
			}
		}
	for(var k in u)
		this[k]=u[k]
	}
n.prototype=this.u
for(var k in v)
	this.users[k] = new n(v[k])

},//fe
genPosterInfo:function(lite,l,ip,uid,fid,cLen){
var u = this.users[uid],
ifAdmin = u.bit_data & 4,
repu=null,
medal = u.medal
if(medal){
	medal = medal.toString().split(',')
	for(var k=0; k<medal.length; k++){
		if(medal[k])
			medal[k] = this.medals[medal[k]]
		}
	}
if(this.reputations){
	var repu={},tmp = this.reputations
	for(var k in tmp)
		repu[k] = {n:tmp[k][0], r:tmp[k][uid]}
	}

return this.self.commonui.dispUserInfo(
	lite,l,
	cLen>=15 ? u.avatar : null,
	u.honor,
	u.regdate,
	u.thisvisit,
	this.groups[u.memberid][1] & 16,
	ip,
	this.groups[u.memberid][0],
	u.mute_time,
	medal,
	u.postnum,
	Math.floor(u.rvrc/10),
	repu,
	uid,
	fid,
	u.money,
	u.site,
	u.bit_data & 1,
	u.username,
	u.yz,
	u.buffs)
}//fe
}//ce
/*
 *生成用户信息
 *@param lite 是否是精简模式
 *@param l 楼层
 *@param avatar 头像
 *@param honor 头衔
 *@param regdate 注册时间
 *@param lastlogin 登陆时间
 *@param lpic 是否有任意级别管理权限
 *@param userip ip地址
 *@param level 用户组
 *@param mute_time 禁言到期时间
 *@param obj/ medal 徽章
 *@param postnum 发帖数
 *@param aurvrc 威望
 *@param obj/ r 声望
 *@param authorid 用户id
 *@param fid 当前fid
 *@param money 铜币
 *@param site 用户的个人版名
 *@param remark 是否有备注
 *@param authorname 用户名
 *@param buff 用户名
 */
commonui.dispUserInfo=function(lite,l,avatar,honor,regdate,lastlogin,lpic,userip,level,mute_time,medal,postnum,aurvrc,r,authorid,fid,money,site,remark,authorname,active,buff){

var x = '',tmp = '',tmp2 = '',d={}

if (avatar){
	if(buff[99])
		avatar = '<img src="'+window.__PORTRAIT_PATH+'/a_sheep.png" style="border:none"/>'
	else
		avatar=commonui.loadPostPortrait(avatar)
	if(avatar){
		if(!lite)
			d.avatar="<div id='postportrait"+l+"' class='portrait' name='portrait'>"+avatar+"</div>"
		else
			d.avatar=avatar.replace(/http:\/\/pic1\.178\.com\/avatars\/(\d+)\/(\d+)\/(\d+)\/(\d+)_(\d+)\.jpg/ig,'http://pic1.178.com/avatars/$1/$2/$3/25_$5.jpg').replace('img src=',"img class='avatar_small' style='margin:-4px 0 -10px 0;border:1px solid #000;width:25px;height:25px;border-radius:5px' src=")
		}
	}

if (honor){
	if (honor.substr(0,1)==' '){
		honor = honor.split(' ');
		honor[1] = parseInt(honor[1]);
		if(honor[1] && honor[1]>__NOW)
			honor = honor[2]
		else if(honor[3])
			honor=honor[3]
		else
			honor=''
		}
	d.honor="<span class='silver' name='honor'>"+honor+"</span>"
	}
if(lpic)
	d.icon = 'sikle_bg.gif'
else if(aurvrc>=0)
	d.icon = 'nga_bg.gif'
else
	d.icon = 'skeleton_bg.gif'

d.l="<a name='l"+l+"' href='"+window.location.href.replace(/#.+$/,'')+'#l'+l+"' class='blue right'>["+l+"楼]</a>"


var w=0;
if(r){
	var y,z
	d.r_bar = {}
	for(var k in r){
		if (r[k]['r']==0)continue;
		if (r[k]['r']>21000)r[k]['r']=21000
		if (r[k]['r']<-21000)r[k]['r']=-21000
		if(k==fid)w=r[k]['r'];
		z = y = Math.abs(r[k]['r']/1000)
		y=Math.floor(y)
		if (z==y){
			y--;
			z=100;
			}
		else{
			z=(z-y)*100
			if (z<1)z=1
			}
		if(!lite)
			d.r_bar[k]="<table style='width:100%'><tbody><tr><td style='width:31px;padding:0'>声望:</td><td style='padding:0 4px 0 2px'><div class='r_container' title='"+r[k]['n']+' &emsp;'+r[k]['r']+"'><table cellspacing=1 class='"+((r[k]['r']>0)?'blue':'red')+"' "+((y)?"style='margin-bottom:-1px'":'')+"><tbody><td class='r_barc'><div style='width:"+z+"%' class=r_bar></div></td></tr></tbody></table>"
		else{
			if(r[k]['r']>9999)
				tmp = 'numeric'
			else
				tmp = 'numericl'
			d.r_bar[k]="<span title='"+r[k]['n']+"'>声望: <span class='"+tmp+"'>"+r[k]['r']+"</span></span>"
			continue
			}

		if(y){
			d.r_bar[k]+="<table style='width:100%' cellspacing=1 class='"+((r[k]['r']>0)?'blue':'red')+"'><tbody><tr>"
			for (var i=0;i<y;i++)
				d.r_bar[k]+="<td class='dot'></td>"
			d.r_bar[k]+="</tr></tbody></table>"
			}

		d.r_bar[k]+="</div></td></tr></tbody></table>"
		}
	}


tmp=tmp2=''
if (mute_time>__NOW){
	tmp=[' orange','(MUTED)']
	tmp2 = "禁言至:"+commonui.time2date(mute_time,'Y-m-d H:i')
	}

if (fid<0 && window.__CUSTOM_LEVEL ){
	z=__CUSTOM_LEVEL[0].n
	for (var i=0;i<__CUSTOM_LEVEL.length;i++){
		if (w>=__CUSTOM_LEVEL[i].r)
			z=__CUSTOM_LEVEL[i].n
		else
			break
		}
	if(w>9999)
		y = 'numeric'
	else
		y = 'numericl'
	if(!lite){
		d.level ="<span title='论坛级别:"+level+" &emsp;威望:"+aurvrc+" "+tmp2+"'>级别: <span name='level' class='silver'>"+z+"</span></span>"
		d.r_value = "声望: <span class='"+y+"'>"+w+"</span>"
		}
	else{
		d.level ="<span title='论坛级别:"+level+" &emsp;威望:"+aurvrc+" "+tmp2+"'>级别: <span name='level' class='silver'>"+z+"</span></span>"
		d.r_value=''
		}
	}
else
	{
	z=''
	d.level = "级别: <span name='level' class='"+tmp+" silver' title='"+tmp2+"'>"+level+"</span>"
	d.r_value = "威望: <span id='posterpg"+l+"' class='numericl silver' name='pg'>"+aurvrc+"</span>"+z
	}

if(active<0)
	tmp = [' darkred','(NUKED)']

d.authorname = "<a href='nuke.php?func=ucp&uid="+authorid+"' id='postauthor"+l+"' class='author b"+(tmp?tmp[0]:'')+"'>"+authorname+(tmp?tmp[1]:'')+"</a><i class='numeric'>("+authorid+")</i>"

if(postnum>9999)
	tmp = 'numeric'
else
	tmp = 'numericl'
if(!isNaN(regdate-0))
	regdate = commonui.time2date(regdate,'Y-m-d H:i')
if(!isNaN(lastlogin-0))
	lastlogin = commonui.time2date(lastlogin,'Y-m-d H:i')
d.postnum="<span title=' 注册时间: "+regdate+" &#10; 最后登陆: "+lastlogin+" '>发帖: <span class='"+tmp+" silver' name='postnum'>"+postnum+"</span></span>"

if(money>0)
	money = "财富: <span class='numericl silver' name='money' value='"+money+"'>"+commonui.calc_money(money)+"</span>";
else
	money=''

if (commonui.dispUserInfoOtherCredit)
	money+=commonui.dispUserInfoOtherCredit(authorid);

d.money = money

if (medal && typeof medal=='object'){
	d.medal="徽章:<span name='medal'>"
	for (var k in medal){
		if(k=='length' || !medal[k])continue
		d.medal+=" <img class='medalimg' src='_.gif' onload='w_i(\""+__IMGPATH+"/medal/"+(medal[k][0] ? medal[k][0] : medal[k].icon)+"\",this)' title='"+(medal[k][1] ? medal[k][1] : medal[k].name)+":&#10;"+(medal[k][2] ? medal[k][2] : medal[k].dscp)+"' style='margin-bottom:-4px'/>"
		}
	d.medal+="</span>"
	}

if (site)
	d.site = "<a href='thread.php?fid=-"+authorid+"' name='site'>["+site+"]</a>"

if(!lite){
	
	if(d.authorname)x+="<div style='margin:0 0 6px 0;line-height:25px;text-align:left'>"+d.l+d.authorname+"</div>"
	
	if(d.avatar)x+=d.avatar

	if(d.honor)x+=d.honor
	x+="<div class='stat'><img src='about:blank' class='x' onerror='w_s(\"background:url("+__IMG_STYLE+"/level/"+d.icon+") top right no-repeat\",this)'/><div style='width:100%'>"

	if(d.r_bar){
		for (var k in d.r_bar)
			x+="<div>"+d.r_bar[k]+"</div>"
		}
	x+="<div style='float:left;margin-right:3px;min-width:49%;*width:49%'><nobr>"+d.level+"</nobr></div>"
	x+="<div style='float:left;margin-right:3px'><nobr>"+d.r_value+"</nobr></div>"
	x+="<div style='float:left;margin-right:3px;min-width:49%;*width:49%'><nobr>"+d.postnum+"</nobr></div>"
	x+="<div style='float:left'><nobr>"+d.money+"</nobr></div>"
	x+="<div class='clear'></div></div>"

	if(d.medal)x+=d.medal+'<br/>'

	if(d.site)x+=d.site+'<br/>'

	if (remark && window.__GP.lesser)
		x+="<img src='about:blank' onerror='commonui.load_user_remark(this,"+authorid+")'/>"

	x+'<div class=clear></div></div><div class=stat_spacer></div>'

	}
else {
	x+=d.l
	if(d.avatar)
		x+=d.avatar+' '
	if(d.authorname)
		x+=d.authorname+' '
	if(d.honor)x+=d.honor+' '
	if(d.r_bar){
		for (var k in d.r_bar)
			x+=d.r_bar[k]+' '
		}
	x+=d.level+" "+d.r_value+" "+d.postnum+" "+d.money+" "

	if(d.medal)x+=d.medal+' '

	if(d.site)x+=d.site+' '

	if (remark && window.__GP.lesser)
		x+="<img src='about:blank' onerror='commonui.load_user_remark(this,"+authorid+")'/>"
	}
return x
}//fe


//签名显示=====================

commonui.loadPostSignCheckHeight = function (o,noimg,lesser,rvrc,is_sig,tid,pid,aid){
if (typeof(o)=='string')o = $(o)
if (o.constructor==Object)o = o.c//见ubbcode.bbsCode参数
if(o.style.display=='none')o.style.display=''
window.setTimeout(function(){
	commonui._debug.push('h '+aid+' '+o.offsetHeight)
	/*if (o.offsetHeight>300){
		o.innerHTML='<span class="gray">签名过大</span>'
		}*/
	o.style.visibility=''
	},1000);
}//fe

commonui.loadPostSign = function(cSig,noimg,lesser,rvrc,is_sig,tid,pid,aid,lite,txt)
{
if(typeof(cSig)=='string')
	cSig = $(cSig)

var arg = {
	c:cSig,
	noImg:noimg,
	tId:tid,
	pId:pid,
	authorId:aid,
	rvrc:rvrc,
	isSig:is_sig,
	callBack:this.loadPostSignCheckHeight,
	isLesser:lesser,
	txt:txt ? txt : cSig.innerHTML
	}

if (lite){
	var y = _$('<button type="button">点击显示签名</button>')._.on('click',function(){
		this.parentNode.visibility='hidden'
		ubbcode.bbsCode.call(ubbcode,arg);
		})
	_$(cSig)._.attr('innerHTML','')._.aC(y)._.css('display','')
	return
	}

cSig.style.visibility='hidden'
ubbcode.bbsCode.call(ubbcode,arg);
}

//logpost=================
commonui.logpost = function (e,tid,pid)
{
this.createadminwindow()
this.adminwindow._.addContent(null)
this.adminwindow._.addContent( "\
<div><div><div><form action='nuke.php?func=logpost&tid="+tid+"&pid="+pid+"' target='commonuiwindowiframe' method='post' onsubmit='commonui.disableInputOnSubmit(this)'><table>\
		<tr>\
			<td>\
				<input name='submit' value='举报此帖' type='submit'> <input value='关闭' type='button' onclick='commonui.hideAdminWindow()'>\
			</td>\
		</tr>\
	</table>\
	<iframe name='commonuiwindowiframe' id='commonuiwindowiframe' onreadystatechange='commonui.remuseInputAfterSubmit(this)' scrolling='no' allowtransparency='true' src='about:blank' frameBorder='0' style='height:50px;width:200px;border:none;overflow:hidden'></iframe>\
	</form>\
</div></div></div>\
")
tTip.showdscp(e,this.adminwindow);

}//fe

//批量操作选择数目限制=================
commonui.massAdminFormCount = function(f)
{
for (var i=0;i<f.elements.length;i++)
	{
	if (f.elements[i].type=='checkbox' && f.elements[i].name=='tidarray[]')
		{
		f.elements[i].onclick=function(){
			var x = this.form.elements;
			var y = 0;
			for (var j=0; j<x.length; j++)
				{
				if (x[j].type=='checkbox' && x[j].checked)y++;
				if (y>15)
					{
					this.checked=false;
					window.alert('你不能选择更多了')
					return false;
					}
				}
			}
		}
	}
}//fe

//防止重复提交=================
commonui.remuseInputAfterSubmit = function(o)
{
var f = o.parentNode;
if (f.nodeName=='FORM'){
	if (o.readyState=='complete')
	{
		f.submiting = false;
		var i = f.getElementsByTagName('input');
		for (var j=0; j<i.length; j++){
			if (i[j].getAttribute('type')=='submit'){
				i[j].disabled = false;
			}
		}
	}
}
}//fe

//防止重复提交=================
commonui.disableInputOnSubmit = function(o)
{
if (o.submiting){return false;}
o.submiting = true;
var i = o.getElementsByTagName('input');
for (var j=0; j<i.length; j++){
		if (i[j].getAttribute('type')=='submit'){
			i[j].disabled = true;
		}
	}
}//fe

//logpost==================
commonui.logPost = function (e,tid,pid)
{
this.createadminwindow()
this.adminwindow._.addContent(null)
this.adminwindow._.addContent( "\
<form action='nuke.php?func=logpost&tid="+tid+"&pid="+pid+"&log' target='commonuiwindowiframe' method='post' style='display:none'></form><button type='button' onclick='this.previousSibling.submit();this.disabled=1'>向版主举报此贴</button> <button type='button' onclick='commonui.hideAdminWindow()'>取消</button><br/>\
<iframe name='commonuiwindowiframe' id='commonuiwindowiframe' onreadystatechange='commonui.remuseInputAfterSubmit(this)' scrolling='no' allowtransparency='true' src='about:blank' frameBorder='0' style='height:50px;width:200px;border:none;overflow:hidden'></iframe>\
")
tTip.showdscp(e,this.adminwindow);
}//fe

//==================================
//显示设置
//==================================

//字体设置===============
commonui.setfont = function(f){
var t ={
	2:{0:'',1:'默认字体',2:'系统默认中文字体'},
	1:{0:'Microsoft Yahei, Verdana, Tahoma, Arial, sans-serif',1:'雅黑字体',2:'微软雅黑'}
	}
f=parseInt(f,10)
if (!isNaN(f))
	{
	cookieFuncs.setMiscCookieInSecond('globalfont',f,3600*24*30);
	window.location.href=window.location.href
	return;
	}
var f = parseInt(cookieFuncs.getMiscCookie('globalfont'),10);
if (!f){
	f=2
	if (__UA[2]==1){
		if (__UA[3]>=6)
			f=1;
		}
	else
		f=1;
	}
this.setfont.setFontValue = f
if(f && document.body.style.fontFamily!=t[f][0])document.body.style.fontFamily=t[f][0];
var s = "<select title='全局字体选择' onchange='commonui.setfont(this.options[this.selectedIndex].value)'>";
for (var k in t)
	{
	s+="<option ";
	if(k==f)s+=" selected ";
	s+="value='"+k+"' title="+t[k][2]+">"+t[k][1]+"</option>";
	}
s+="</select>";
return s
}//fe

//用户脚本设置===============
commonui.ifuserscript = function(r){
if (!domStorageFuncs)return
if (!__CURRENT_UID)return
var ck = 'user_script_'+__CURRENT_UID
var t ={
	0:{0:'不使用用户脚本',1:'不使用用户脚本'},
	1:{0:'使用用户脚本',1:'使用己设置的用户脚本(在用户中心中设置)'}
	}
r=parseInt(r,10)
if (!isNaN(r))
	{
	if(r)cookieFuncs.setMiscCookieInSecond(ck,r,3600*24*30);
	else cookieFuncs.setMiscCookieInSecond(ck,0,0);
	window.location.href=window.location.href
	return;
	}
var r = parseInt(cookieFuncs.getMiscCookie(ck))
if (!r)r=0;
if (r){
	var s = domStorageFuncs.get(__CURRENT_UID+'_user_script');
	if (s){
		eval('var tmp=function(){'+s+'}')
		this.aE(window,'DOMContentLoaded',function(){var x = window.setTimeout(tmp,1500)});
		}
	}
var s = "<select title='是否用户脚本' onchange='commonui.ifuserscript(this.options[this.selectedIndex].value)'>";
for (var k in t)
	{
	s+="<option ";
	if(k==r)s+=" selected ";
	s+="value='"+k+"' title="+t[k][1]+">"+t[k][0]+"</option>";
	}
s+="</select>";
return s
}//fe

//内嵌窗口阅读===============
commonui.iframeread = function(r){
var t ={
	0:{0:'默认方式阅读主题',1:'点击链接跳转到阅读主题页面'},
	1:{0:'内嵌窗口阅读主题',1:'点击链接打开阅读主题内嵌窗口'}
	}
r=parseInt(r,10)
if (!isNaN(r))
	{
	if(r)cookieFuncs.setMiscCookieInSecond('iframeread',r,3600*24*30);
	else cookieFuncs.setMiscCookieInSecond('iframeread',0,0);
	window.location.href=window.location.href
	return;
	}
var r=parseInt(cookieFuncs.getMiscCookie('iframeread'),10);
if (!r)r=0;
var s = "<select title='阅读主题方式选择' onchange='commonui.iframeread(this.options[this.selectedIndex].value)'>";
for (var k in t)
	{
	s+="<option ";
	if(k==r)s+=" selected ";
	s+="value='"+k+"' title="+t[k][1]+">"+t[k][0]+"</option>";
	}
s+="</select>";
return s
}//fe

//不显示签名和头像===============
commonui.picswitch = function(r){
var t ={
	0:{0:'显示签名和头像',1:'阅读主题页面中显示签名和头像'},
	1:{0:'不显示签名和头像',1:'阅读主题页面中不显示签名和头像'}
	}
r=parseInt(r,10)
if (!isNaN(r))
	{
	if(r)cookieFuncs.setMiscCookieInSecond('notLoadPAndS',r,3600*24*30);
	else cookieFuncs.setMiscCookieInSecond('notLoadPAndS',0,0);
	window.location.href=window.location.href
	return;
	}
var r=window.__LITE.notLoadPAndS ? 1 : 0
var s = "<select title='显示签名和头像选择' onchange='commonui.picswitch(this.options[this.selectedIndex].value)'>";
for (var k in t)
	{
	s+="<option ";
	if(k==r)s+=" selected ";
	s+="value='"+k+"' title="+t[k][1]+">"+t[k][0]+"</option>";
	}
s+="</select>";
return s
}//fe


//==================================
//历史链接
//==================================

//锁定历史链接==============
commonui.lockViewHis = function (kk,lock){
var h = commonui.userCache.get('ForumViewHis'),x=null
for (var i in h){
	if (h[i] && h[i][0]==kk){
		if (lock){
			x=h[i]
			x[2]=1
			}
		else{
			x=h[i]
			if (x[2])delete x[2]
			}
		commonui.userCache.set('ForumViewHis',h,3600*24*30)
		return
		}
	}
}//fe

//加入历史链接==============
commonui.addForumViewHis = function(n,id)
{
var h = commonui.userCache.get('ForumViewHis')

if (h && typeof(h[0])=='object'){
	if (h[0][0]==id){
		if(h[0][2])
			return true
		else
			return
		}
	var l = null,m=null,f=null,x=0,limit=21;
	for (var j=limit;j>=0;j--){
		if(h[j]){
			if(m===null)m=j
			if(h[j][2]){
				if(id==h[j][0])
					return true
				}
			else{
				if(l===null)l=j
				if (id==h[j][0]){
					l=j
					f=true
					break;
					}
				}
			}
		}
	if (f)
		x=l
	else{
		if (m<limit)
			x=m+1
		else{
			if (l)
				x=l
			else
				return
			}
		}
	for (j=x;j>0;j--)
		h[j]=h[j-1]
	h[0] = {0:id,1:n}
	}
else
	h={0:{0:id,1:n}};
commonui.userCache.set('ForumViewHis',h,3600*24*30);
}//fe


//生成历史访问链接==============
commonui.advNav = function (o){
var h = commonui.userCache.get('ForumViewHis')
if (!h || !h[0]) return;
var x = _$(o.getElementsByTagName('a')[0])._.on('mouseout',function(e){
	var to = e.relatedTarget || e.toElement;
	if(to && to!=y && to.parentNode!=y && to.parentNode.parentNode!=y)y.style.display='none'
	})._.on('mouseover',function(){
	if(this.offsetHeight)y.style.marginTop=(window.__TOUCH ? this.offsetHeight : Math.floor(this.offsetHeight*0.94))+'px';
	y.style.display='block'
	})
, y = _$('<div/>')._.cls('urltip urltip2 navhisurltip')._.css('paddingLeft','3px')._.on('mouseout',function(e){
	var to = e.relatedTarget || e.toElement;
	if(to && to!=this && to.parentNode!=this && to.parentNode.parentNode!=this)this.style.display='none'
	})
, fid=0;
for (var k in h){
	if (h[k][2]) 
		y.innerHTML+="<a href='javascript:void(0)' onclick='commonui.lockViewHis(\""+h[k][0]+"\",false);this.firstChild.src=this.firstChild.src.replace(\"/star.gif\",\"/stargray.gif\")' title='点击解除锁定'><img src='"+__IMG_STYLE+"/star.gif'/></a> "
	else
		y.innerHTML+="<a href='javascript:void(0)' onclick='commonui.lockViewHis(\""+h[k][0]+"\",true);this.firstChild.src=this.firstChild.src.replace(\"/stargray.gif\",\"/star.gif\")' title='锁定这个链接 并添加到论坛首页快速导航中'><img src='"+__IMG_STYLE+"/stargray.gif'/></a> "
	y.innerHTML+="<a href='/thread.php?fid="+h[k][0]+"'>"+h[k][1]+"</a><br/>"
	}
x.parentNode.insertBefore(y,x.parentNode.firstChild)
if(window.__TOUCH)
	commonui.doubleclick.init(x)
}//fe

//==================================
//主题分类
//==================================

//分类转为连接============
commonui.topicLinkTipLoad = function (o){
var x = []
o.innerHTML=o.innerHTML.replace(/\[.+?\]/g,function($0){
	x.push($0);
	if(window.__TOPIC_KEY_COLOR && __TOPIC_KEY_COLOR[$0])
		return '<span class="t_k_c'+__TOPIC_KEY_COLOR[$0][1]+'">'+$0+'</span>'
	else
		return '<span class="silver">'+$0+'</span>'})
if (x.length){
	if(window.__CURRENT_FID)var z = '&fid='+__CURRENT_FID
	else z=''
	var y=''
	for (var i=0; i<x.length; i++)
		y += "<a href='thread.php?key="+x[i]+z+"'>"+x[i]+"</a> "
	this.genTip.add(o,y,{margin:-17})
	}
}//fe


//获取版面的分类==================
commonui.onloadtopic_key = function(o,fid){
var ffid = false
if (o.form && o.form.elements.namedItem('fid') && o.form.elements.namedItem('fid').value)
	ffid = o.form.elements.namedItem('fid').value
if (ffid)
	{
	if (o._last_fid==ffid)return
	fid=o._last_fid = ffid
	}
else
	{
		if (!fid){
			window.alert('你必须选择一个版面');
			return
		}
		if (o.onchange)
			return;
	}
o.disabled=true
o.innerHTML='';
var x = document.createElement('option');
x.innerHTML = '加载中...'
x.value=''
o.appendChild(x)
httpDataGetter.script_muti_get(Array('/data/bbscache/bbs_topic_key/'+fid+'.js?'+date.getHours(),'/nuke.php?func=loadtopickey&fid='+fid+'&time='+date.getHours()),
	function(r){
		if (!r) return false;
		else
			{
			if ((date.getTime()/1000-r.time)>3600){return false;}
			o.options[0].innerHTML = '...'
			r = r.data;
			for (var k in r)
				{
				var x = document.createElement('option');
				x.value=r[k].key
				x.innerHTML=r[k].key
				if (r[k].top) x.style.backgroundColor='#eee'
				o.appendChild(x)
				}
			o.onchange=function()
				{
				if (o.form.elements.namedItem("post_subject"))var x=o.form.elements.namedItem("post_subject")
				else if(o.form.elements.namedItem("key"))var x = o.form.elements.namedItem("key")
				x.value=o.value+" "+x.value
				}
			o.disabled=false
			o.onclick = function(){commonui.onloadtopic_key(this)}
			return true
			}
		},
	function(){o.disabled=false},
	'gbk'
	);
}//fe


//==================================
//版面杂项ui
//==================================

//加载置顶==============
commonui.parseToppedTopic = function(o){
o.style.display='none'
if(window.__LITE && window.__LITE.embed)o.innerHTML = '[collapse]'+o.innerHTML+'[/collapse]'
o.innerHTML = commonui.parseImgInToppedTopic(o.innerHTML).replace(/\s*<br\s*\/?>\s*\[size=\d+%\]\s*\[\/size\]/gi,'').replace(/\[quote\]|\[\/quote\]/gi,'').replace(/^(\s*<br\s*\/?>\s*)+|(\s*<br\s*\/?>\s*)+$/gi,'')
_$(o)._.on('click',function(e){
	var o = e.target ? e.target : e.srcElement
	if(o.name=='collapseButton' || o.name=='randomblockButton')this.style.height=this.style.maxHeight=''
	})._.css((o.innerHTML.match(/\[headline\]/i)) ? {height:'290px',display:'block',overflow:'auto'} : {maxHeight:'700px',display:'block',overflow:'auto'})
ubbcode.bbscode(o)
o.style.display='block'

}//fe


//窗帘生成 =============
commonui.imgInToppedTopic={}
commonui.parseImgInToppedTopic = function(txt){

var s={
'img':[[312,250],[312,250]],
'iframe':[[312,576],[312,475]]
}
var j=0,mm=[]

txt = txt.replace(/\[(iframe|img|flash|headline)=?([0-9,]+)?\](.+?)\[\/\1\]/ig , function($0,$1,$2,$3){
	if($1=='headline')return $0
	if(j>1 || $1=='flash')return '['+$1+' not load]'
	var w=0, h=0
	$1=$1.toLowerCase()
	if($3.match(/\.jpg|\.jpeg|\.png|\.gif|\.bmp$/i))
		$1=='iframe'
	if ($1=='iframe' && $3 && ubbcode.checklink($3,1)<1)
		{
		if($2){
			$2=$2.split(',')
			if($2[1]){
				w = $2[0]
				h = $2[1]
				}
			}
		if(w==0 || w>s[$1][j][0])w=s[$1][j][0]
		if(h==0 || h>s[$1][j][1])h=s[$1][j][1]
		mm[j]="<iframe style='width:"+w+"px;height:"+h+"px;overflow:hidden;border:none' frameBorder=0 src='"+$3+"'></iframe>";
		}
	else if ($1=='img'&& $3)
		{
		if ($3.substr(0,2)=='./')
			$3 = commonui.getAttachBase($3)+'/'+$3.substr(2);
		w=s[$1][j][0]
		h=s[$1][j][1]
		mm[j]="<div style='width:"+w+"px;height:"+h+"px;overflow:hidden;text-align:center'><img src='"+$3+"' alt='' onerror='this.nextSibling.style.display=\"inline\"'/><span class='silver' style='display:none'> [ "+$3+" ] </span></div>";
		}
	j++
	return ''
	})

commonui.imgInToppedTopic=mm
return txt
}//fe

//子版面tab==============
commonui.setforumtab = function (o,fid,recmd,admin,user)
{
var tab = o.getElementsByTagName('h2')
var i = 0
if (tab[i].className.indexOf('parentforum')!=-1)
	i++;
tab[i].className='a'
tab[i+1].className=tab[i+2].className='ia'
if (fid){
	if (user||admin)
		{
		tab[i].className='ia'
		if (admin)
			tab[i+1].className='a'
		else
			tab[i+2].className='a'
		}
	}
else
	tab[i+1].style.display=tab[i+2].style.display='none'
}


//排序链接==============
commonui.setThreadOrder = function (o,oo)
{
var x = window.location.href.toLowerCase()
x = x.replace(/(?:(\?)|&)order_by=\w*/,'$1')
if (o=='postdatedesc' || o=='lastpostdesc'){
	if (x.substr(x.length-1)=='?')
		x+='order_by='+o
	else
		x+='&order_by='+o
	}
oo.href=x;
oo.onclick=function(){};
return true;
}//fe

//添加主题信息==============
commonui.loadThreadInfo = function(self,o_replies,o_topic,o_author,o_ptime,o_replier,o_rtime
	,now,newtime,ifupload,digest,quote_from,quote_tid,postdate,lastpost,replies,locked,font,avatar,select_box,tid,o_pagelinks,type){

if (font){
	font = font.split('~')
	if(font[1])font[0] += ' b'
	if(font[2])font[0] += ' italic'
	o_topic.className+=' '+font[0];
	}

type=parseInt(type,10)

var x = o_pagelinks.getElementsByTagName('a')
for (var i=0; i<x.length; i++)
	this.addFrom2Therad(x[i])
this.addFrom2Therad(o_topic)
this.addFrom2Therad(o_rtime)

commonui.topicLinkTipLoad(o_topic);



if (window.__CURRENT_FID && window.__SELECTED_FORUM && o_topic.parentNode.parentNode.className.indexOf('ufindex')!=-1){
	var tmp = o_topic.parentNode.getElementsByTagName('a')
	var x = null
	for (var k=0;k<tmp.length;k++){
		if (tmp[k].className.indexOf('nomatch')!=-1){
			x = tmp[k]
			break;
			}
		}
	if(x){
		tmp = x.href.match(/thread\.php\?fid=(\d+)/)
		if(tmp){
			commonui.genTip.add(x,"<a href='javascript:void(0)' class='red' onclick='commonui.unionforumSubscribe("+tmp[1]+",__CURRENT_FID,2)'>不在主题列表中显示这个版面</a>")
			x.parentNode.style.float='right'
			}
		}
	}

var tmp = document.createElement('span');
if(quote_from)
	tmp.innerHTML+="<span class='gray nobr'>[<a href='read.php?tid="+tid+"' class='gray'>镜像</a>]</span>";
if (digest)
	tmp.innerHTML+="<span class='blue nobr'>[精华]</span>";
if ((type & 1024)!==0)
	tmp.innerHTML+="<span class='red nobr'>[锁定]</span>";
if ((type & 2)!==0)
	tmp.innerHTML+="<span class='red nobr'>[隐藏]</span>";

if (now-lastpost<=newtime)
	tmp.innerHTML+="<span class='orange nobr'>[新帖]</span>";
if (__GP['lesser'] && lastpost>postdate && (lastpost-postdate)<24*3600)
	tmp.innerHTML+="<span class='orange nobr' title='平均回复间隔时间'>["+Math.floor((lastpost-postdate)/replies)+"]</span>";
if (ifupload)
	tmp.innerHTML+="<b class='orange'>+</b>";
o_topic.parentNode.insertBefore(tmp,o_topic.nextSibling);

o_replies.style.display=o_ptime.style.display=o_rtime.style.display='none'

o_ptime.innerHTML = this.time2dis(postdate)
o_rtime.innerHTML = this.time2dis(lastpost)
o_ptime.title = this.time2date(postdate,'y-m-d H:i')
o_rtime.title = this.time2date(lastpost,'y-m-d H:i')

if (this.setfont.setFontValue==1)//雅黑11px
	o_rtime.style.fontSize=o_ptime.style.fontSize=o_author.style.fontSize=o_replier.style.fontSize='11px'
else{
	if (o_ptime.innerHTML.match(/^[0-9\-: ]+$/))
		o_ptime.style.fontSize='11px'
	if (o_rtime.innerHTML.match(/^[0-9\-: ]+$/))
		o_rtime.style.fontSize='11px'
	}

var s=20
if(replies>99){
	if(replies>999){
		if(replies>9999)
			s=13
		else
			s=14
		}
	else
		s=16
	}
o_replies.style.fontSize=s+'px'

var bgC,tmp = o_ptime.parentNode.parentNode.className+' '+o_ptime.parentNode.className
if(typeof(this.loadThreadInfo.bgColor[tmp])=='undefined'){
	bgC = this.getStyle(o_ptime.parentNode,'background-color')
	if(bgC){
		if(bgC.substr(0,1)=='#')
			bgC = commonui.hexToRgb(bgC)
		else
			bgC = bgC.match(/\d+/g)
		bgC = this.rgbToHsv(bgC[0],bgC[1],bgC[2])
		}
	this.loadThreadInfo.bgColor[tmp] =bgC
	}
else
	bgC = this.loadThreadInfo.bgColor[tmp]

if(locked)
	o_replies.parentNode.style.backgroundColor=o_replies.style.color='#eee'
else{
	if(replies>100)replies=100
	if(bgC){
		if(commonui.genReplyColor)
			var x=commonui.genReplyColor(bgC,replies)
		else
			var x=commonui.genReplyColorDefault(bgC,replies)
		o_replies.style.color='rgb('+x[0]+','+x[1]+','+x[2]+')'
		}
	else
		o_replies.style.color='#eee'
	}

if (select_box){
	var select_box = document.createElement('input')
	select_box.type='checkbox'
	select_box.className='checkbox right'
	select_box.value=tid
	select_box.onclick = function(){commonui.massAdmin.check(this,this.value)}
	}

if(window.__PARALLEL){
	o_replies.parentNode.style.display=o_replier.parentNode.style.display='none'
	var y=o_ptime.parentNode , o_replier = o_replier.cloneNode(1) , tmp= document.createElement('div')

	var z = _$('<div>'+
		o_replies.innerHTML.replace(/(\d+)/g,"<span style='font-family:Arial black;font-weight:900;letter-spacing:-1px'>$1</span>")+
		"</br><span style='font-size:"+(o_rtime.style.fontSize ? o_rtime.style.fontSize : '12px')+"'>"+
		o_rtime.innerHTML.replace(/(\d+)/g,"<span style='font-family:Arial black;font-weight:900;letter-spacing:-1px'>$1</span>")+
		"</span></div>")._.css({fontSize:'35px',height:'40px',lineHeight:'20px',textAlign:'right',marginBottom:'-40px',paddingRight:'2px',color:o_replies.style.color})

	y.insertBefore(z,y.childNodes[0])
	o_replier.className+=' silver'
	o_replier.style.display='block'
	y.appendChild(o_replier)
	y.style.width='23%'
	y.style.textAlign='left'
	y.style.paddingLeft='6px'

	if (avatar && !window.isIE6)
		this.loadThreadInfoAvatar(avatar,o_author.parentNode,{0:o_author,1:o_replier},this.hsvToRgb(bgC[0],bgC[1],bgC[2]))

	commonui.genTip.add(y.childNodes[0],
		"<span class=silver>发布 : </span>"+o_ptime.innerHTML+"<br/><span class=silver>回复 : </span>"+o_rtime.innerHTML
		,{triggerElm:o_author,hide:1})
	
	var o_ptime={style:{}}
	this.loadThreadInfo.count++

	if (select_box){
		select_box.style.marginTop='0'
		o_topic.parentNode.insertBefore(select_box,o_topic.parentNode.childNodes[0])
		}
	}
else{
	if (select_box)
		o_replies.parentNode.insertBefore(select_box,o_replies)
	if (avatar && !window.isIE6 && !window.__LITE.notLoadPAndS)
		this.loadThreadInfoAvatar(avatar,o_ptime.parentNode,{0:o_author,1:o_ptime},this.hsvToRgb(bgC[0],bgC[1],bgC[2]))
	}

o_replies.style.display=o_ptime.style.display=o_rtime.style.display=''
self.parentNode.removeChild(self);
}//fe
commonui.loadThreadInfo.bgColor={}
commonui.loadThreadInfo.count=0

//回复的颜色
commonui.genReplyColorDefault = function(bgC,replies){
if(replies>100)replies=100
var x = 1/600*replies,y=bgC[1]+x+0.005
x=bgC[0]-x-0.065
x = this.hsvToRgb(x<0?x+1:x,y>1?y-1:y,bgC[2])
return x
}

//添加来源参数
commonui.addFrom2Therad = function(o){
o.href+= (window.__CURRENT_FID && window.__UNION_FORUM? '&_ff='+__CURRENT_FID : '')  +  (window.__CURRENT_PAGE && __CURRENT_PAGE!=1? '&_fp='+__CURRENT_PAGE : '')  +  (window.__CURRENT_ORDER ? '&forder_by='+__CURRENT_ORDER : '')
}

//双排时调整栏头========================
commonui.loadThreadHead = function (self,r,t,pt,rt){
if (window.__PARALLEL){
	r.style.display=rt.style.display='none'
	pt.style.width='23%'
	pt.style.paddingLeft=pt.style.paddingRight='0px'
	for (var i=0;i<rt.childNodes.length;i++){
		if(rt.childNodes[i]!==self)
			pt.appendChild(rt.childNodes[i].cloneNode(true))
		}
	}
self.parentNode.removeChild(self);
}//fe

//双排时对齐高度========================
commonui.alignHeight = function (a,b,c){
if(this.loadThreadInfo.count<c){
	var self=this
	window.setTimeout(function (){
		self.alignHeight(a,b,c)
		},50)
	return
	}
var x = a.getElementsByTagName('tr'),y=b.getElementsByTagName('tr'),z = Math.max(x.length,y.length),e,f,g,h,j
for (var i=0;i<z;i++){
	if(x[i] && y[i]){
		var c = x[i].getElementsByTagName('td'),d= y[i].getElementsByTagName('td')
		if (c[1].offsetHeight>d[1].offsetHeight){
			e=d[1]
			f=c[1].offsetHeight
			}
		else if(c[1].offsetHeight<d[1].offsetHeight){
			e=c[1]
			f=d[1].offsetHeight
			}
		else
			continue
		g=parseInt(commonui.getStyle(e,'border-top-width'),10)
		if(!h){
			h=parseInt(commonui.getStyle(e,'padding-top'),10)
			j=parseInt(commonui.getStyle(e,'padding-bottom'),10)
			if(!h)h=0
			if(!j)j=0
			}
		e.style.height = (f-h-j-(g?g:0))+'px'
		}
	}
}//fe


//显示主题列表的头像========================



commonui.loadThreadInfoAvatar = function (a,td,shw,bgC){

a = commonui.selectUserPortrait(a,1)

var mask = document.createElement('div')
mask.style.margin = '-6px -1px -95px -1px'
mask.style.height='100px'
mask.style.backgroundImage = 'url("'+__IMG_STYLE+'/avatarmask'+( (td.parentNode.className.indexOf('row1')!=-1)? 1 : 2 )+'.png")'

var img = new Image()
img.onreadystatechange=img.onload=function(){
	if(this&&this.readyState&&this.readyState!='complete')
		return
	this.onreadystatechange = this.onload=null
	var x = Math.floor(this.width*(a.cX ? a.cX : 0.25)-17),y = Math.floor(this.height*(a.cX ? a.cX : 0.3)-17)
	td.style.backgroundPosition='-'+x+'px -'+y+'px'
	td.style.backgroundImage='url("'+a+'")'
	if((this.width-x)<110){//渐变mask宽度
		x = 110-(this.width-x)
		mask.style.backgroundPosition='-'+x+'px 0px'
		}
	if(x=parseInt(commonui.getStyle(td,'padding-left'),10))
		mask.style.marginLeft = '-'+x+'px'
	}
img.src = a
td.style.backgroundRepeat='repeat-y'
td.style.verticalAlign='top'
td.style.overflow='hidden'
td.insertBefore(mask,td.childNodes[0])
for (var k in shw){
	if(window.__UA && __UA[0]==1){
		shw[k].style.backgroundColor='#'+bgC[0].toString(16)+bgC[1].toString(16)+bgC[2].toString(16)
		shw[k].style.display='block'
		shw[k].style.zoom=1
		shw[k].style.filter='progid:DXImageTransform.Microsoft.Chroma(Color='+shw[k].style.backgroundColor+') progid:DXImageTransform.Microsoft.DropShadow(color=#ffffff, offX=1, offY=1);'
		}
	else
		shw[k].style.textShadow='1px 1px 0 #fff'
	}
}//fe


//添加版主信息==============
commonui.genadminlist = function(t,fid)
{
if (t){
	t = t.split(',');
	var i = 0;
	var l ='';
	for (var k in t){
		if (t[k]){
			l= l+"<option value='"+t[k]+"'>"+t[k]+"</option>";
			i++;
			}
		}
	if (l){
		t="版主: <select onchange='if(this.selectedIndex>0){window.location.href=\"nuke.php?func=ucp&username=\"+this.options[this.selectedIndex].value}'><option value=''>...</option>"+l+"</select>";
		}
	}
else t='';
if (fid) fid="<a href='nuke.php?func=view_privilege&fid="+fid+"'>[版面权限]</a>"
else fid='';
put("<span class='moderator'>"+fid+' '+t+"</span>");
}//fe

//==================================
//阅读杂项ui
//==================================

//翻页跳转==============
commonui.jumpToForm = function (postPerPage,tid,l)
{
if (l){
	var x = window.location.href+'&';
	return x.replace(/(\?|&)page=(?:e|\d+)(&|#|$)/gi,'$1$2').replace(/#.*$/i,'')+'page='+Math.ceil ((parseInt(l,10)+1)/postPerPage)+'#l'+l;
	}
else if(tid && postPerPage){
	put(" &nbsp;<form action='' method='post' onsubmit='this.action=commonui.jumpToForm("+postPerPage+",0,this.getElementsByTagName(\"input\")[0].value)'>到<input type='text' size='2'/><button type='submit'><span>楼</span></button></form>");
	}
}//fe

//主题状态==============
commonui.dispTopicStat = function (d,l){
var x = ''
l=parseInt(l,10)
d=parseInt(d,10)
if (d)
	x+="<span class='red'>[精华]</span>"
if (l==1)
	x+="<span class='red'>[锁定]</span>"
if (l==2)
	x+="<span class='red'>[关闭]</span>"
return x
}//fe

//==================================
//杂项ui
//==================================

//rss==============
//rss==============
commonui.rssLinkGen = function (needAuth,fid,n){

var u = window.location.search,i = __IMG_STYLE+"/rss1.gif",r='',a='RSS',g='添加到Google',star=this.addForumViewHis(n,fid)


if (star)
	star ="<a href='javascript:void(0)' onclick='commonui.lockViewHis(\""+fid+"\",false);this.firstChild.src=this.firstChild.src.replace(\"/star.gif\",\"/stargray.gif\")' title='从论坛首页快速导航中移除'><img src='"+__IMG_STYLE+"/star.gif' style='vertical-align: -1px'/></a> "
else
	star ="<a href='javascript:void(0)' onclick='commonui.lockViewHis(\""+fid+"\",true);this.firstChild.src=this.firstChild.src.replace(\"/stargray.gif\",\"/star.gif\")' title='添加到论坛首页快速导航中'><img src='"+__IMG_STYLE+"/stargray.gif' style='vertical-align: -1px'/></a> "


u+='&rss=1'
if (needAuth){
	u+='&ngaPassportUid='+cookieFuncs.getCookie('ngaPassportUid')+'&ngaPassportCid='+cookieFuncs.getCookie('ngaPassportCid')
	a = '(版面需验证 此rss会在退出登录后失效)'
	g+=' '+a
	i = __IMG_STYLE+"/rssgray1.gif"
	}

put( star+" <a href='"+u+"'  title='"+a+"'><img style='vertical-align: -1px' src='"+i+"'/></a> <a href='http://fusion.google.com/add?source=atgs&feedurl="+encodeURIComponent(u)+"&rss=1' title='"+g+"'><img style='vertical-align: -1px' src='"+__IMG_STYLE+"/gadd.gif'/></a>" );
}//fe


function onloadforumlist(o){
commonui.onloadforumlist(o)
}//fe
//版面选择生成==============
commonui.onloadforumlist=function(o)
{
o.onclick=null
o.disabled=true
o.name='fid';
o.options.innerHTML='';
o.options.length=0;
var x = document.createElement('option');
x.value=''
x.innerHTML='加载中...'
o.appendChild(x)
var load_admin=0,load_lesser=1,load_his=0
	function tmp(sub,pf,bg)
	{
	for (var k in sub)
		{
		var x = document.createElement('option');
		x.value=sub[k].fid
		x.innerHTML=pf+sub[k].name
		if (bg) x.style.backgroundColor=bg
		o.appendChild(x)
		if (sub[k].sub)
			{
			tmp(sub[k].sub,pf+'&emsp;')
			}
		}
	}
	function tmp2(n,bg)
	{
	var x = document.createElement('option');
	x.value=''
	x.innerHTML=n
	x.style.backgroundColor=bg
	o.appendChild(x)
	}
	function tmp3(){
	o.options[0].innerHTML='...';
	o.disabled=false;
	}
var h =  commonui.userCache.get('ForumViewHis');
if (h)
	{
	var hh = {}
	tmp2('最近访问过的版面','#aaa')
	for (var k in h)
		hh[k] = {'fid':h[k][0],'name':h[k][1]}
	tmp(hh,'','#ddd')
	}
if (load_admin && load_lesser)tmp3()
load_his=1
/*
if (__GP['lesser'])
	{
	httpDataGetter.script_muti_get("/nuke.php?func=loadforumselect",
		function(r){
		if (!r)
			{
			return false;
			}
		else
			{
			r = r.data;
			if (r)
				{
				tmp2('全部','#aaa')
				for (var k in r)
					{
					if (r[k]['fup'] && r[r[k]['fup']])
						{
							if (!r[r[k]['fup']].sub)r[r[k]['fup']].sub={}
							if (!r[r[k]['fup']].sub[k])r[r[k]['fup']].sub[k] = r[k];
							delete r[k];
						}
					}
				tmp(r,'','')
				}
			if (load_admin && load_his)tmp3()
			load_lesser=1
			return true
			}
		},
		function(){
			if (load_admin && load_his)tmp3()
			load_lesser=1
		},
		'gbk'
		);
	}
else
	load_lesser=1
*/
httpDataGetter.script_muti_get("/nuke.php?func=loadforumselectbyadimin",
	function(r){
	if (!r)
		{
		return false;
		}
	else
		{
		r = r.data;
		if (r)
			{
			tmp2('我管理的版面','#aaa')
			for (var k in r)
				{
				if (r[k]['fup'] && r[r[k]['fup']])
					{
						if (!r[r[k]['fup']].sub)r[r[k]['fup']].sub={}
						if (!r[r[k]['fup']].sub[k])r[r[k]['fup']].sub[k] = r[k];
						delete r[k];
					}
				}
			tmp(r,'','#eee')
			}
		if (load_lesser && load_his)tmp3()
		load_admin=1
		return true
		}
	},
	function(){
		if (load_lesser && load_his)tmp3()
		load_admin=1
	},
	'gbk'
	);
}//fe

//PM链接生成==============
commonui.loadPmIconSub=function (o,r,iconnew,iconno,iconuser){
if (r.data.total>0){
	if((r.data.message>0 || r.data.announcement>0) && iconuser)
		o.src=iconuser
	else
		o.src=iconnew
	o.title='您有新消息 消息:'+r.data.message+' 系统:'+r.data.system+' 公告:'+r.data.announcement
	}
else{
	o.src=iconno
	o.title='短消息'
	}
}//fe
commonui.loadPmIcon=function(o,iconnew,iconno,uid,iconuser){
o.onload=function(){}
var r = commonui.userCache.get('pmStatCache')
if (r)
	return this.loadPmIconSub(o,r,iconnew,iconno,iconuser)
httpDataGetter.script_muti_get("http://interface.i.178.com/?_app=sms&_controller=index&_action=check_new&rtype=2&uid="+uid+"&"+Math.floor(date.getTime()/100000),
	function(r){
	if (!r || !r.result)
		return false;
	else{
		commonui.userCache.set('pmStatCache',r,180)
		commonui.loadPmIconSub(o,r,iconnew,iconno,iconuser)
		}
	return true
	},
	function(){
	o.src=''
	o.title=o.alt='get error'
	},
	'gbk',
	'___json___'
	)
}//fe

//投票显示==================
commonui.vote = function(o,tid){
httpDataGetter.script_muti_get("/nuke.php?func=vote2&tid="+tid+"&"+Math.floor(date.getTime()/100),
	function(r){
	if (!r || !r.data)
		return false;
	var sum = 0,id='vote'+Math.random(),max=1,type='radio',txt="<table id='"+id+"'><tbody>"
	if (r.data.max_select){
		max = r.data.max_select
		delete r.data.max_select
		if(max>1)type='checkbox'
		}
	for (var k in r.data)
		sum+=r.data[k].count
	for (var k in r.data){
		txt += "<tr><td>"+r.data[k].info+"</td>";
		if ( r.checksum )
			{
			txt += "<td><input type='"+type+"' name='vote"+tid+"' value='"+k+"' onclick='commonui.vote.check(this,"+max+")'/></td>";
			}
		txt += "<td><b>"+r.data[k].count+"票</b><td style='width:200px'><div style='background:#790000;height:10px;width:"+((sum)? r.data[k].count/sum*100 : 0)+"%'></div></td></tr>";
		}
	if (r.checksum)
		txt += "<tr><td colspan=3><button type='button' onclick='commonui.vote.submit(this,"+tid+",\""+r.checksum+"\")'>投票</button><td></tr>"
	txt += "</tbody></table>"
	o.innerHTML = txt
	return true
	},
	function(){
	o.innerHTML='get error'
	},
	'gbk'
	)
}//fe

commonui.vote.check = function (o,max){
var x = o.parentNode.parentNode.parentNode.getElementsByTagName("input"),j=0;
for (var i = 0;i<x.length;i++){
	if (x[i].type=='checkbox' && x[i].checked){
		j++
		if(j>max){
			o.checked=false
			window.alert('你不能选择超过'+max+'个')
			return false
			}
		}
	}
}//fe

commonui.vote.submit = function (o,tid,checksum){
var x = o.parentNode.parentNode.parentNode.getElementsByTagName("input"),y=[]
for (var i = 0;i<x.length;i++){
	if (x[i].checked){
		y.push(x[i].value)
		}
	}
if (y.length)
	window.location.href="nuke.php?func=vote2&tid="+tid+"&check="+checksum+'&voteid='+y.join(',')
}//fe




//读取主题列表==================
commonui.loadmostuserrecommendbyfid = function (x,fid,day,nocache){
commonui.loadtopic_js(x,Array(__CACHE_PATH+'/load_topic_cache/mostuserrecommend_'+fid+'_'+day+'.js?'+date.getDate()+date.getHours(),'nuke.php?func=loadtopic&js=1&f=mostuserrecommend&fid='+fid+'&day='+day+'&timeout='+3600*2.1),3600*2.1);
}

commonui.loadhotbyfid = function (x,fid,nocache){
commonui.loadtopic_js(x,Array(__CACHE_PATH+'/load_topic_cache/hot_'+fid+'_.js?'+date.getDate()+date.getHours(),'nuke.php?func=loadtopic&js=1&f=hot&fid='+fid+'&timeout='+3600*2.1),3600*2.1);
}

commonui.loadtodaydelbyfid = function (x,fid,nocache){
commonui.loadtopic_js(x,Array(__CACHE_PATH+'/load_topic_cache/todaydel_'+fid+'_.js?'+date.getDate()+date.getHours(),'nuke.php?func=loadtopic&js=1&f=todaydel&fid='+fid+'&timeout='+3600*1.1),3600*1.1,false);
}

commonui.loadtopic_js = function (x,url,timeout,randomOrder,subjectLength)
{
if(typeof(x)=='string')x=$(x)
if (x.innerHTML!='')return
if (!subjectLength) subjectLength=21;
if (typeof(randomOrder)=='undefined') randomOrder=true;
this.loadtopic_js_v2({domObj:x,url:url,timeout:timeout,randomOrder:randomOrder,subjectLength:subjectLength,postPrePage:null,styleFunc:this.topicRender.style_2})
}

// 参数x的属性包括
//domObj, 列表容器
//url,取数据的地址 可以是一个字符串 或包含多个url的数组(详见httpDataGetter.script_muti_get)
//timeout,数据超时时间
//randomOrder,随机排序
//subjectLength,主题长度限制
//postPrePage,主题每页显示帖子数
//styleFunc,样式函数 见commonui.topicRender 
//onLoadFunc(domObj),加载结束后执行的
//currentPage,当前页 有值将增加翻页链接
//getPageUrlFunc(url,page)输入当前的url(既上面的url属性) 返回指定page的url
commonui.loadtopic_js_v2 = function (x)
{
var self=this;
if(typeof(x.url)=='string')x.url = Array(x.url)
if(!x.url[1])x.timeout=0;
x.domObj.style.visibility='hidden'
httpDataGetter.script_muti_get(
x.url,
function(data){
	if (!data)return false;
	if (x.timeout && (__NOW-data.time)>x.timeout)return false;
	var html=[],t,dk,d,nextPage;
	if (x.currentPage){
		//0 如果没指定获得翻页url的函数 则使用默认的
		if (!x.getPageUrlFunc)
			x.getPageUrlFunc=function(url,newPage){
				var u,uu=[]
				if(typeof(url)=='string')u=[url]
				else u=url
				for(var i=0;i<u.length;i++){
					if(u[i].indexOf('page=')!=-1)
						uu[i]=u[i].replace(/page=([0-9]+)/,'page='+newPage)
					else
						uu[i]=url[i]+'&page='+newPage
					}
				return uu
				}//fe
		//0 --------------
		var mp = 0;
		if(!data.nextPage)mp = x.currentPage
		nextPage = self.topicRender.listPager(x.currentPage,mp,function(a){commonui.loadtopic_js_v2(a)},function (p){
			a = __NUKE.inheritClone(x)
			a.currentPage = p
			a.url=x.getPageUrlFunc(a.url,p)
			return a
			})//calle
		}
	if (Object.prototype.toString.call(data.data) != '[object Array]'){
		d=[]
		for (var k in data.data) d.push(data.data[k])
		}
	else
		d=data.data
	if (x.randomOrder)
		d = d.sort(function(){return Math.random()-0.5});
	for (var k=0;k<d.length;k++){
		dk = d[k]
		if (!dk.tid && dk._id)dk.tid=dk._id;
		t={}
		t.i=k
		if(dk.hot)t.hot=dk.hot
		t.subject=self.topicRender.subject(dk.tid,dk.subject,x.subjectLength,dk.titlefont,dk.quote_from)
		if(typeof(dk.replies)!='undefined' && x.postPrePage) t.page=self.topicRender.pager(dk.tid,dk.replies,x.postPrePage)
		if(dk.fid){
			t.fid=dk.fid
			if(data.forumName)
				t.forum=self.topicRender.forum(dk.fid,data.forumName[dk.fid])
			else if(__SUBSCRIPTIONS)
				t.forum=self.topicRender.forum(dk.fid,__SUBSCRIPTIONS[dk.fid])
			}
		if(dk.authorid && dk.author) t.author=self.topicRender.author(dk.authorid,dk.author)
		if(dk.lastposter) t.replier=self.topicRender.author(null,dk.lastposter)
		if(dk.postdate) t.ptime=dk.postdate
		if(dk.lastpost) t.rtime=dk.lastpost
		html.push(x.styleFunc(t));
		}
	x.domObj.innerHTML='<ul>'+html.join('')+'</ul>';
	if(nextPage){
		x.domObj.appendChild(nextPage)
		x.domObj.insertBefore(nextPage.cloneNode(true),x.domObj.firstChild)
		}
	if(x.onLoadFunc)
		x.onLoadFunc(x.domObj)
	x.domObj.style.visibility='visible'
	return true;
	},
function(){
	x.domObj.innerHTML='读取错误';
	},
	{'charset':'gbk','noCache':true}
);

}




commonui.topicRender = {

style_1:function (t){
if (t.i&1==1)t.i='b2'
else t.i='b1'
var x= ''
if(__SUBSCRIPTIONS_COLOR && t.fid)x=" style='border-color:"+__SUBSCRIPTIONS_COLOR['_'+t.fid]+"'"
t.ptime = commonui.topicRender.date(t.ptime ,'m-d H:i')
t.rtime = commonui.topicRender.date(t.rtime ,'m-d H:i',1)
return "\
<li class='"+t.i+"'"+x+">\
<span class='right' title='最后回复'>"+t.replier+" "+t.rtime+"</span>\
"+t.subject+" "+t.page+"\
<span class='subinfo block'>\
<span class='right' title='发布时间'>"+t.author+" "+t.ptime+"</span>\
"+t.forum+"\
</span>\
</li>";
},//fe

style_2:function (t){
if (t.i&1==1)t.i='b2'
else t.i='b1'
t.ptime = commonui.topicRender.date(t.ptime)
return "<li class='"+t.i+"'><span class='subinfo'>"+t.hot+" "+t.ptime+"</span>"+t.subject+"</li>"
},//fe

date:function (time,format,_2dis,cls){
if(!format)format='y-m-d H:i'
if(!cls)cls='date'
if(_2dis)
	return "<span class='"+cls+"'>"+commonui.time2dis(time,format)+"</span>"
else
	return "<span class='"+cls+"'>"+commonui.time2date(time,format)+"</span>"
},//fe

listPager:function(currentPage,maxPage,handler,getHandlerArgFunc){
if (maxPage){
	if (maxPage<=1) return _$('<span/>')._.cls('page_nav');
	uncertain=''
	}
else
	{
	maxPage = currentPage+1
	uncertain ='可能有此页';
	}
var tmp = function(p,t,d,c){
	return _$('<a>'+t+'</a>')._.cls(c)._.attr('title',d)._.attr('href','javascript:void(0)')._.sV('pagerHandler',{handler:handler,arg:getHandlerArgFunc(p)})._.on('click',function (){
	var h = this._.gV('pagerHandler')
	h.handler(h.arg)
	})
	}

var pager = _$('<span/>')._.cls('page_nav');

pager._.aC(tmp(1,'&lt;&lt;','第一页','ls-4 first'))
if((currentPage-1)>0)pager._.aC(tmp(currentPage-1,'&lt;','上一页('+(currentPage-1)+')','prev'))
var j=0;
for(var i=currentPage-3;i<=currentPage-1;i++){
	if(i<1) continue;
	pager._.aC(tmp(i,i,'','pl'))
	}
pager._.aC(tmp(currentPage,currentPage,'','b current'))
if(currentPage<maxPage){
	for(var i=currentPage+1;i<=maxPage;i++){
		if (uncertain && i==maxPage)
			pager._.aC(tmp(i,i,uncertain,'pr'))
		else
			pager._.aC(tmp(i,i,'','pr'))
		j++;
		if(j==4) break;
		}
	i--;
	if (uncertain){
		pager._.aC(tmp(i,'&gt;',uncertain+'('+i+')','next'))
		pager._.aC(tmp(i,'','','end'))
		}
		else{
		pager._.aC(tmp(i,'&gt;','下一页('+i+')','next'))
		pager._.aC(tmp(i,'&gt;&gt;','最后页('+maxPage+')','last ls-4'))
		}
	
	}
pager._.aC( _$('<button>刷新</button>')._.attr('type','button')._.sV('pagerHandler',{handler:handler,arg:getHandlerArgFunc(currentPage)})._.on('click',function (){
	var h = this._.gV('pagerHandler')
	h.handler(h.arg)
	}) )
return pager
},

pager:function (tid,p,postPrePage,blank,cls){
if (p < postPrePage)return '';
page='';
if(!cls)cls='gray'
blank= blank ? ' target="_blank"' : ''
if ((p+1)%postPrePage==0)
	var maxp=(p+1)/postPrePage;
else
	var maxp=Math.floor((p+1)/postPrePage)+1;
page+=" <span class='pager'>[ ";
for (var j=1; j<=maxp; j++)
	{
	if (j==6)
		{
		page+=" ... <a class="+cls+" href='/read.php?tid="+tid+"&page="+maxp+"'"+blank+">"+maxp+"</a>";
		break;
		}
	page+=" <a style='color:gray' href='/read.php?tid="+tid+"&page="+j+"'"+blank+">"+j+"</a>";
	}
page+=' ]</span>';
return page
},//fe

subject:function (tid,subject,lengthMax,titlefont,quoteFrom,blank,cls){
if(!cls)cls='topic'
blank= blank ? ' target="_blank"' : ''
var til=''
if(lengthMax && subject.length>lengthMax){
	til=' title="'+subject+'" '
	subject = subject.substr(0,lengthMax)+'...';
	}
if(titlefont){
	titlefont=titlefont.split("~");
	if(titlefont[0])subject="<span class='"+titlefont[0]+"'>"+subject+"</span>";
	if(titlefont[1])subject="<b>"+subject+"</b>";
	if(titlefont[2])subject="<i>"+subject+"</i>";
	if(titlefont[3])subject="<u>"+subject+"</u>";
	}
else if (quoteFrom)
	subject="<b>"+subject+"</b>";
return "<a class='"+cls+"' href='/read.php?tid="+tid+"'"+blank+til+">"+subject+"</a>"
},//fe

author:function (uid,name,blank,cls){
if(!cls)cls='author'
if(uid)
	return "<a href='/nuke.php?func=ucp&uid="+uid+"' class='"+cls+"'"+blank+">"+name+"</a>"
else
	return "<span class='"+cls+"'>"+name+"</span>"
},//fe

forum:function (fid,name,blank,cls){
if(!cls)cls='forum'
blank= blank ? ' target="_blank"' : ''
return "<a href='/thread.php?fid="+fid+"' class='"+cls+"'"+blank+">[ "+name+" ]</a>"
}//fe
}//ce

//论坛信息==================
commonui.getBoardInfo = function (o,totalinbbs,userinbbs,unvalidateuser,guestinbbs){
httpDataGetter.script_muti_get(
'/nuke.php?func=custom_index&f=info',
function(data){
	if (!data)return false;
	o.innerHTML = "<table style='width:100%' cellpadding='0px' cellspacing='0px'><tr>\
	<td style='text-align:left;vertical-align:bottom;'>\
		<div class='nav'><strong>"+data['notice']+"</strong></div>\
	</td>\
	<td style='text-align:right;line-height:18px'>\
		共 <span class='numeric'>"+totalinbbs+"</span> 人在线,<span class='numeric'>"+userinbbs+" <span title='未验证'>("+unvalidateuser+")</span></span> 位会员,<span class='numeric'>"+guestinbbs+"</span> 位访客<br/>\
		最多 <span class='numeric'>"+data['higholnum']+"</span> 人 <span class='numeric'>("+time2date(data['higholtime'])+")</span><br/>\
		共 <span class='numeric'>"+data['threads']+"</span> 篇主题,<span class='numeric'>"+data['posts']+"</span>  篇帖子,<span class='numeric'>"+data['members']+"</span>  位会员<br/>\
		<a href='thread.php?authorid="+__CURRENT_UID+"&date=all'>我的主题</a> · <a href='thread.php?recommend=1&date=all'>精华区</a> · <a href='/thread.php?favor=1'>我的收藏</a> · 欢迎新会员 <a href='profile.php?uid="+data['newmember']['uid']+"' class='green'>"+data['newmember']['username']+"</a>\
	</td>\
</tr></table>";
	return true;
	},
function(){
	o.innerHTML='读取错误';
	},
'gbk'
);
}//fe

/**
*获取附件地址
*@param u 附件相对地址 
*/
commonui.getAttachBase=function(u){
if(u.substr(0,2)!='./')
	u='./'+u
var m = u.match(/^\.\/mon_(\d+)\/(\d+)/)
if(m){
	var b = (window.__BBSURL=='http://bbs.ngacn.cc') ? true : false
	if(parseInt(m[1].toString()+m[2].toString(),10)>=20130104){
		if(b)
			return 'http://img6.ngacn.cc/attachments'
		else
			return 'http://img6.nga.178.com/attachments'
		}
	else{
		if(b)
			return 'http://img.ngacn.cc/attachments'
		else
			return 'http://ngaimg.178.com/attachments'
		}
	}
return ''
}
/**
*地址是否是附件
*@param u 地址 
*/
commonui.ifUrlAttach = function(u){
u = u.substr(0,20)
if(u=='http://img.ngacn.cc/' || u=='http://img6.ngacn.cc' || u=='http://ngaimg.178.com' || u=='http://img6.nga.178.com')
	return true
}

//帖子操作按钮==================
commonui.postBtn={
saveKey:'postBtnHis',
d:{
1:{u:'/nuke.php?func=topicrecommend&tid={tid}',n1:'支持',n2:'推荐主题，版主推荐、加分、精华、标题加亮等有额外加成',
	ck:function(a){if (!a.pid && __GP['rvrc']>20 && a.authorid!=__CURRENT_UID)return 1} },
//2:{u:'nuke.php?func=post_recommend&tid={tid}&pid={pid}',n1:'反对',n2:'反对这个回帖，会降低发帖者的声望',
//	ck:function(a){if (a.pid && a.pid!='tpc'&& __GP['rvrc']>=0 && a.authorid!=__CURRENT_UID)return 1} },
//3:{u:'nuke.php?func=post_recommend&tid={tid}&pid={pid}&good=1',n1:'支持',n2:'支持这个回帖，会提高发帖者的声望',
//	ck:function(a){if (a.pid && a.pid!='tpc'&& __GP['rvrc']>=0 && a.authorid!=__CURRENT_UID)return 1} },
4:{u:'/read.php?pid={pid}&to',n1:'主题',n2:'跳转至主题内阅读此贴',c:'red',
	ck:function(a){if (a.pid && window.location.href.indexOf('pid=')!=-1)return 1} },
5:{n1:'举报',n2:'举报此贴至版主',on:function(e,a){commonui.logPost(e,a.tid,a.pid)},
	ck:function(a){if ( __GP['rvrc']>-50 || __GP['admincheck'])return 1} },
6:{u:'/post.php?action=modify&fid={fid}&tid={tid}&pid={pid}&article={lou}',n1:'编辑',
	ck:function(a){if (a.authorid==__CURRENT_UID || __GP['admincheck'])return 1} },
7:{u:'/post.php?action=quote&fid={fid}&tid={tid}&pid={pid}&article={lou}',n1:'引用',
	ck:function(a){if (__CURRENT_UID)return 1} },
8:{u:'/post.php?action=reply&fid={fid}&tid={tid}&pid={pid}&article={lou}',n1:'回复',
	ck:function(a){if (__CURRENT_UID)return 1} },
9:{n1:'评论',n2:'做一个简短的回复 / 贴纸条',on:function(e,a){commonui.comment(e,a.tid,a.pid)},
	ck:function(a){if (__CURRENT_UID)return 1} },
10:{n1:'评分',on:function(e,a){adminui.addpoint(e,a.tid,a.pid,a.fid)},
	ck:function(a){if ( (a.fid<0 && __GP['admincheck']) || (a.fid>0 && __GP['admincheck'] && __GP['greater']))return 1} },
11:{u:'/nuke.php?func=set_user_reputation&uid={authorid}&fid={fid}',n1:'声望',n2:'设置此人的声望',
	ck:function(a){if (__GP['admincheck'] && a.fid<0)return 1} },
12:{n1:'禁言',on:function(e,a){adminui.muteuser(e,a.authorid)},
	ck:function(a){if (__GP['super'])return 1} },
13:{n1:'Nuke',on:function(e,a){adminui.nukeUi(e,a.authorid)},c:'gray',
	ck:function(a){if (__GP['super'])return 1} },
14:{n1:'Nuke',n2:'禁言并扣减声望',n3:'Lesser Nuke',on:function(e,a){commonui.lessernuke(e,a.tid,a.pid,1,a.lou)},
	ck:function(a){if (__GP['admincheck'] && __GP['greater'])return 1} },
15:{n1:'签名',n2:'清除签名',n3:'清除签名',on:function(e,a){adminui.clearSignUi(e,a.authorid)},
	ck:function(a){if (__GP['greater'])return 1} },
16:{n1:'头像',n2:'清除头像',n3:'清除头像',on:function(e,a){adminui.clearAvatarUi(e,a.authorid)},
	ck:function(a){if (__GP['greater'])return 1} },
17:{n1:'删除',n2:'删除回复',n3:'删除回复',on:function(e,a){commonui.delReplay(e,a.tid,a.pid)},
	ck:function(a){if (__GP['admincheck'] && a.pid && parseInt(a.pid,10) && a.pid>0)return 1} },
18:{n1:'翻译',n2:'以版主提供的术语表进行对照翻译',on:function(e,a,o){if(o._.gV('transed'))return;commonui.autoTranslate.main($('postcontent'+a.lou),a.fid);o._.sV('transed',1)},
	ck:function(a){if (window.__AUTO_TRANS_FID)return 1} },
19:{u:"/nuke.php?func=message#to={authorid}",n1:'短信',n2:'向作者发送短消息',
	ck:function(a){if (__CURRENT_UID)return 1} },
20:{u:"/nuke.php?func=ucp&uid={authorid}",n1:'作者',n2:'查看作者资料',n3:'作者信息'},
21:{u:"/thread.php?authorid={authorid}",n1:'搜索&sup1;',n2:'搜索作者发布的主题',n3:'搜索作者的主题'},
22:{u:"/thread.php?searchpost=1&authorid={authorid}",n1:'搜索&sup2;',n2:'搜索作者发布的回复',n3:'搜索作者的回复'},
23:{u:"/read.php?tid={tid}&authorid={authorid}",n1:'搜索&sup3;',n2:'搜索作者在本主题内的回复',n3:'搜索主题内回复(只看该作者)'},

24:{u:"http://i.178.com/?_app=cite&_controller=index&_action=newcite&type=inner_cite&url=_URL",target:'_blank',n3:"<img src='"+__COMMONRES_PATH+"/_.gif' style='background:url(http://img4.178.com/www/201102/91497211205/91497234247.png) 0px -64px;width:16px;height:16px'/>",n2:'分享到178个人空间',on:function(e,a,o){o.href=commonui.postBtn.replaceUrl(o.href)} },
25:{u:"http://v.t.sina.com.cn/share/share.php?appkey=3938048249&title=_TOPIC&url=_URL",target:'_blank',n3:"<img src='"+__COMMONRES_PATH+"/_.gif' style='background:url(http://img4.178.com/www/201102/91497211205/91497234247.png) 0px -112px;width:16px;height:16px'/>",n2:'分享到新浪微博',on:function(e,a,o){o.href=commonui.postBtn.replaceUrl(o.href)} },
26:{u:"http://v.t.qq.com/share/share.php?title=_TOPIC&url=_URL&appkey=8b5c8745ea364613adfda05c616d9abe",target:'_blank',n3:"<img src='"+__COMMONRES_PATH+"/_.gif' style='background:url(http://img4.178.com/www/201102/91497211205/91497234247.png) 0px -128px;width:16px;height:16px'/>",n2:'分享到腾讯微博',on:function(e,a,o){o.href=commonui.postBtn.replaceUrl(o.href)} },
27:{u:"http://t.163.com/article/user/checkLogin.do?info=_TOPIC - _URL",target:'_blank',n3:"<img src='http://img0.178.com/www/201104/97723162502/97723185658.gif'/>",n2:'分享到网易微博',on:function(e,a,o){o.href=commonui.postBtn.replaceUrl(o.href)} },

28:{u:"javascript:scrollTo(0,0)",n1:((window.__UA && (__UA[2]==1) && (__UA[3]<6))?'&uarr;':'&uArr;'),n2:'回到页面顶端'},

29:{n1:'<input type="checkbox" name="delatc[]" value="0"/>',n2:'选中回复',n3:'(<input type="checkbox" name="delatc[]" value="0"/>选中回复)',
	ck:function(a){if (__GP['admincheck'] && a.pid && parseInt(a.pid,10) && a.pid>0){
		this.n3=this.n3.replace(/value="\d+"/i,'value="'+a.pid+'"')
		this.n1=this.n1.replace(/value="\d+"/i,'value="'+a.pid+'"')
		return 1}}
	,on:function(e,a){this.value=a.pid},tag:'span'},
30:{u:'/nuke.php?func=edit_history&tid={tid}&pid={pid}',n2:'查看编辑记录',n3:'编辑记录',
	ck:function(a){if (a.authorid==__CURRENT_UID || __GP['admincheck'])return 1} },
/*31:{u:'/nuke.php?func=locktopic&edit_lock&tid={tid}&pid={pid}&lock=1',n2:'发帖时间超过时限禁止编辑',n3:'禁止编辑',
	ck:function(a){if (__GP['admincheck'])return 1} },*/
32:{u:'/nuke.php?func=locktopic&edit_lock&tid={tid}&pid={pid}',n2:'设置为发帖时间超过时限可以编辑',n3:'许可编辑',
	ck:function(a){if (__GP['admincheck'])return 1} },
33:{u:window.location.href+'&noBBCode',n2:'查看帖子的BBCode源码',n3:'查看源码' },
34:{n1:'转发',on:function(e,a,o){var x = commonui.postBtn.all;commonui.postBtn.all={'分享':[24,25,26,27]};commonui.postBtn.allBtn(e,a);commonui.postBtn.all=x} }
},

replaceUrl:function (u){
var e = encodeURIComponent;
return u.replace('_TOPIC',e(document.title)).replace('_URL',e(document.location.href)).replace(/_BBSURL/g,e(window.__BBSURL))
},

def:[28,6,7,8,34],
all:{
'帖子':[1,4,5,6,7,8,9,18,33],
'用户':[19,20,21,22,23],
'管理':[10,11,12,13,14,15,16,17,29,32,30],
'分享':[24,25,26,27]
},
btnCache:{},

genU:function(a,u){
return u ? " href='"+u.replace(/\{(.+?)\}/g,function($0,$1){return a[$1]})+"' " : " href='javascript:void(0)' "
},//fe

genT:function(a,t){
return t ? " title='"+t+"' " : ' '
},//fe

genC:function(btn,c){
return btn ? " class='cell rep txtbtnx "+(c?c:'silver')+"' " : " class='b "+(c?c:'silver')+"' "
},

genA:function(a,id,btn){
var d = this.d[id],tag='a',self=this
if((d.ck && !d.ck(a)) || (btn && !d.n1))return null
if(!d.on)d.on=function(){}
if(d.tag)tag=d.tag
btn=_$("<"+tag+" "+this.genU(a,d.u)+this.genC(btn,d.c)+this.genT(a,d.n2)+"><nobr>"+(btn ?  d.n1 : (d.n3 ? d.n3 : d.n1))+"</nobr></"+tag+">")._.on('click',btn?function(e){d.on(e,a,this)}:function(e){d.on(e,a,this),self.saveHis(id)})
if(d.target)btn.target=d.target
return btn
},

genB:function(arg){

var i=0,l=this.def,xx=null,s = _$("<a href='javascript:void(0)' class='sep'></a>"),b=_$("<div class='c2 page_nav postBtnCC' style='float:left'><a href='javascript:void(0)' class='start'></a></div>")
if(!this.his){
	this.his = commonui.userCache.get(this.saveKey);
	if(!this.his)this.his=[]
	}
while(1){
	for (var k=0;k<l.length;k++){
		if(i++>=8)break
		if(xx=this.genA(arg,l[k],1))b._.aC(xx,s.cloneNode(false))
		}
	if(l==this.his)break
	l=this.his
	}

b._.aC(_$("<a href='javascript:void(0)' class='rep txtbtnx gray'><nobr>更多</nobr></a>")._.on('click',function(event){commonui.postBtn.allBtn(event,arg)}))
b._.aC(_$("<a href='javascript:void(0)' class='end'></a>"))
return _$('<div class="postBtnCCC"></div>')._.aC(b)
},//fe

argCache:{},

load:function(o,oo,lou,fid,tid,pid,authorid,tauthorid,recommend,orgforum,have_cmt,is_cmt,posterip,posttime,client){
var x = '',xx='',self=this,arg={
lou:lou,
fid:fid,
tid:tid,
pid:pid,
authorid:authorid,
tauthorid:tauthorid,
recommend:recommend,
orgforum:orgforum,
have_cmt:have_cmt,
is_cmt:is_cmt,
posterip:posterip,
posttime:posttime
} 
, over= function(){
	var b=self.btnCache[arg.lou]
	if(b && b.style.display=='block')return
	for (var k in self.btnCache){
		if(k!=arg.lou && self.btnCache[k])self.btnCache[k].style.display='none'
		}
	if(!b){
		b=self.genB(arg)
		self.btnCache[arg.lou]=b
		b.style.visibility='hidden'
		b.style.display='block'
		document.body.appendChild(b)
		var w = b.offsetWidth+1 //fix ie9/ff4 float point width poblem
		oo.innerHTML=''
		oo.appendChild(b)
		b.style.display='none'
		b._.css({visibility:'',marginLeft:'-'+w+'px',left:'auto',top:'auto',width:w+'px'})
		b.style.display='block'
		}
	else
		b.style.display='block'
	}
, out = function(e){
	if (!e) var e = window.event;
	var t = (window.event) ? e.srcElement : e.target, r = (e.relatedTarget) ? e.relatedTarget : e.toElement;
	while (r && r != this && r.nodeName != 'BODY')
		r= r.parentNode
	if (r==this) return;
	var b=self.btnCache[arg.lou]
	if(b)b.style.display='none'
	}

if(is_cmt)
	this.argCache[pid]=arg
else
	this.argCache[lou]=arg

if(!is_cmt && window.__CURRENT_UID && $('post1strow'+lou)){
	var z = _$('post1strow'+lou)
	z._.on('mouseover',over)
	z._.on('mousewheel',over)
	//z._.on('touchstart ',over)
	z._.on('mouseout',out)
	}

var y=commonui.time2date(posttime,'Y-m-d_ H : i ').split('_')

x+="<span id='postdate"+lou+"' class='b'>"+y[0]+"<br/><span>"+y[1]+"</span></span>"
/*
if (pid!='tpc' && pid!=-1)
	x+="<br/><a href='read.php?pid="+pid+"' class='blue'>["+pid+"]</a>";
else
	x+="<br/><a href='read.php?tid="+tid+"' class='red'>["+tid+"]</a>";
	*/

if (orgforum)
	x+="<br/><span class='silver'>["+orgforum+"]</span>";
if(posterip)
	x+="<br/><span class='silver' style='font-size:9px'>["+posterip+"]</span>";

o.innerHTML="<span style='font-size:10px;line-height:1.4em'>"+x+"</span>"
_$(o)._.aC(_$("<div class='page_nav_s' style='width:60px'><span class='start'></span><a href='javascript:void(0)' class='rep txtbtnx silver b' style='width:50px'>操作</a><span class='end'></span></div>")._.on('click',function(event){commonui.postBtn.allBtn(event,arg)}))
o.style.textAlign='center'
},//fe


allBtn:function(e,arg){
var $=window._$,c = commonui,z=null,x= $('/span').$0('className','ltxt'),y=this.all,s=$('/span').$0('innerHTML',' &nbsp;'),self=this

c.createadminwindow()
c.adminwindow._.addContent(null)

for (var k in y){
	var u=$('/span')
	for (var kk=0;kk<y[k].length;kk++)
		if(z=this.genA(arg,y[k][kk]))u._.aC(z,s.cloneNode(1))
	if(u.childNodes.length)
		x.$0($('/h4').$0('className',"textTitle",'innerHTML',k),u)
	}

x._.aC($('/h4').$0('className',"textTitle",'innerHTML','主题地址'),$('/span').$0('className',"xtxt",'innerHTML',window.location.hostname+'/read.php?tid=<span class=red>'+arg.tid+'</span>'))

if (arg.pid!='tpc' && arg.pid>0)
	x._.aC($('/h4').$0('className',"textTitle",'innerHTML','此回复地址'),$('/span').$0('className',"xtxt",'innerHTML',window.location.hostname+'/read.php?pid=<span class=blue>'+arg.pid+'</span>'))

x._.aC( $('/br') , $('/br') , _$("<button type='button' onclick='commonui.hideAdminWindow()'>关闭</button>") , _$("<button/>")._.attr({type:'button',innerHTML:'清空历史记录'})._.on('click',function(){self.clearHis()}) )

c.adminwindow._.css('width','350px')._.addContent(x)

tTip.showdscp(e,c.adminwindow);
},//fe

saveHis:function(id){
if(!this.d[id] || !this.d[id].n1)return
for (var k=0;k<this.def.length;k++){
	if(this.def[k]==id)return
	}
var x=[],i=0
x.push(id)
for (var k=0;k<this.his.length;k++){
	if(this.d[this.his[k]] && this.d[this.his[k]].n1 && this.his[k]!=id){
		x.push(this.his[k])
		if(i++>=8)break
		}
	}
this.his=x
commonui.userCache.set(this.saveKey,this.his,86400*30);
for (var k in this.btnCache)
	this.btnCache[k]=null
},//fe

clearHis:function(){
commonui.userCache.del(this.saveKey)
this.his=[]
for (var k in this.btnCache)
	this.btnCache[k]=null
}//fe

}//ce

function loadpostfuncbtn(nouse,lou,fid,tid,pid,authorid,tauthorid,recommend,orgforum,have_cmt,is_cmt,posterip){
return commonui.postBtn.load(nouse,lou,fid,tid,pid,authorid,tauthorid,recommend,orgforum,have_cmt,is_cmt,posterip)
}//fe

//主题操作按钮==================
commonui.topicBtn={
saveKey:'topicBtnHis',
d:{
1:{n1:'标题',n2:'编辑标题颜色',n3:'标题颜色',
	on:function(e,a){adminui.colortopic(e,a.tid)},
	ck:function(a){if(a.admin)return 1} },

2:{n1:'锁定',n2:'锁定/关闭主题 除版主外不能回复/阅读',n3:'锁定主题',
	on:function(e,a){adminui.locktopic(e,a.tid)},
	ck:function(a){if(a.admin)return 1} },

3:{n1:'移动',n2:'移动主题到其他版面',n3:'移动主题',
	on:function(e,a){adminui.movetopic(e,a.tid)},
	ck:function(a){if(a.admin)return 1} },

4:{n1:'镜像',n2:'在其他版面创建主题的镜像 在主题被回复时镜像也会更新',n3:'镜像主题',
	on:function(e,a){adminui.quotetopic(e,a.tid)},
	ck:function(a){if(a.anyAdmin)return 1} },

5:{n1:'提前',n2:'将主题的回复时间修改为当前时间',n3:'提前主题',
	on:function(e,a){adminui.pushtopic(e,a.tid)},
	ck:function(a){if(a.admin)return 1} },

6:{n1:'计数',n2:'手动修复在回复被删除后主题的页数错误',n3:'重新统计回复',
	u:'nuke.php?func=recountreply&tid={tid}',
	target:'_blank',
	ck:function(a){if(a.admin)return 1} },

7:{n1:'置顶',n2:'将主题(的内容)显示在版面最上方 作为版面公告',n3:'置顶主题',
	on:function(e,a){adminui.toptopic(e,a.tid)},
	ck:function(a){if(a.admin && !a.minorAdmin)return 1} },

8:{n1:'精华',n2:'将主题设置为精华',n3:'精华主题',
	on:function(e,a){adminui.digesttopic(e,a.tid)},
	ck:function(a){if(a.admin && !a.minorAdmin)return 1} },

9:{n1:'删除',n2:'将主题移入回收站',n3:'删除主题',
	on:function(e,a){adminui.deltopic(e,a.tid)},
	ck:function(a){if((a.admin && !a.minorAdmin) || __CURRENT_UID == 17387322)return 1} }
},

replaceUrl:commonui.postBtn.replaceUrl,

def:[],
all:{'主题操作':[1,2,3,4,5,6,7,8,9]},

genU:commonui.postBtn.genU,

genT:commonui.postBtn.genT,

genC:commonui.postBtn.genC,

genA:commonui.postBtn.genA,

load:function(o,oo,tid,admin,anyAdmin,minorAdmin){
if(!admin && !anyAdmin && __CURRENT_UID != 17387322)return
if(!window.adminui)loader.script(__COMMONRES_PATH+'/js_admin.js')
var arg={tid:tid,admin:admin,anyAdmin:anyAdmin,minorAdmin:minorAdmin,pid:0}, i=0,l=this.def,xx=null,s = _$("<a href='javascript:void(0)' class='sep'></a>"),self=this

o.insertBefore(_$("<a href='javascript:void(0)' class='rep txtbtnx' title='更多主题操作功能'>操作</a>")._.on('click',function(e){self.allBtn(e,arg)}),oo)

if(!this.his){
	this.his = commonui.userCache.get(this.saveKey);
	if(!this.his)this.his=[]
	}
while(1){
	for (var k=0;k<l.length;k++){
		if(i++>=4)break
		if(xx=this.genA(arg,l[k],1)){
			o.insertBefore(s.cloneNode(false),oo)
			o.insertBefore(xx,oo)
			}
		}
	if(l==this.his)break
	l=this.his
	}
o.insertBefore(s.cloneNode(false),oo)
},//fe


allBtn:commonui.postBtn.allBtn,//fe

saveHis:commonui.postBtn.saveHis,//fe

clearHis:commonui.postBtn.clearHis//fe

}//ce

//用户头像选择==================
//{t:(int)type, l:(int)length, 0:{0:(str)avatar,cX:(int)centerX,cY:(int)centerY}, 1:(str)avatar  }
commonui.selectUserPortrait = function(a,type){
if(!a)return ''
var x,i=0
if (typeof(a)=='string' && a.substr(0,1)!='{'){
	if(a.indexOf('|')!=-1)return '';
	else x=a
	}
else{
	if(typeof(a)=='string' && a.substr(0,1)=='{'){
		var a = 'var a='+a
		eval(a)
		}
	if(a && typeof(a)=='object'){
		if (a.t==1 && !type)
			i=Math.floor(Math.random()*a.l)
		else if (a.t==2 && window.date && !type){
			var tmp = (date.getHours()+8)/24
			if (tmp>1)tmp = tmp-1
			i=Math.floor(tmp*a.l)
			}
		x=a[i]
		}
	else
		return ''
	}
if(typeof(x)=='object'){
	var y = new String(x[0])
	y.cX = x.cX
	y.cY = x.cY
	y.id = i
	return y
	}
else{
	x = new String(x)
	x.id = i
	return x
	}
}

//论坛菜单当前用户头像选择========
commonui.loadCurUserPortrait = function(p){
if (p){
	p = this.selectUserPortrait(p)
		if (p.substr(0,4) != 'http')
			return (__PORTRAIT_PATH+'/'+p);
		else
			return (p);
	}
else
	return (__IMG_STYLE+'/nobody.gif');
}

//用户头像与信息==================
commonui.loadPostPortrait = function(a){
if (window.__LITE.notLoadPAndS){
	commonui.loadPostPortrait=function(){return ''}
	return ''
	}
return "<img src='"+this.selectUserPortrait(a)+"'/>";
}




//生成用户名的连接=========================

/*
 * 用户名连接增加tooltip
 * @param 用户名连接的node(tip node加在此node之前
 * @param 用户id
 * @param 是否加载i178信息
 */
commonui.usernamelink = function(o,uid,loadi){
if(!o)return
if(window.__GP && __GP['admincheck']){
	var x = this.colorChar(o.innerHTML)
	if(x!=o.innerHTML){
		o.style.display='none'
		o.parentNode.appendChild(_$('<b>'+x+'</b>'))
		}
	}
o=o.parentNode
var y = document.createElement('span')
y.className='urltip2 urltip3'
y.style.textAlign='left'
y.innerHTML += "<nobr><a href='http://i.178.com/?_app=index&_controller=index&_action=index&uid="+uid+"'>[178用户中心]</a> <a href='/nuke.php?func=ucp&uid="+uid+"'>[论坛资料]</a> <a target='_blank' href='http://t.178.com/"+uid+"'>[t.178.com]</a></nobr>"
o.parentNode.insertBefore(y,o)
if(!loadi)y._loadedt178info=true
_$(o)._.on('mouseover',function(){

	y._t178infotimeout = window.setTimeout(function(){
		y.style.display='block'
		y.style.marginTop='-'+y.offsetHeight+'px'
		if (!y._loadedt178info){
			if(typeof(y._loadedt178info)=='object')
				commonui.usernameLinkSub(y._loadedt178info,uid,y)
			else{
				y._loadedt178info=true
				httpDataGetter.script_muti_get("http://t.178.com/api/nga/get_nga_card?user_id="+uid,
					function(r){
						if(!r || !r.nickname)
							return
						y._loadedt178info = r
						commonui.usernameLinkSub(r,uid,y)
						},
					function(){}
					)
				}
			}
		},600)

	})
_$(o.parentNode)._.on('mouseout',function(e){
	if(__NUKE.ifMouseLeave(e,this)){
		if(y._t178infotimeout)
			window.clearTimeout(y._t178infotimeout)
		y.style.display='none'
		}
	})
if(window.__TOUCH)
	commonui.doubleclick.init(o)
}//fe


commonui.usernameLinkSub=function(r,uid,y){

if(!r.avatar)
	r.avatar = 'http://pic1.178.com/avatars/00/8e/5b/50_9329482.jpg'
else
	r.avatar=r.avatar.replace(/\/\d+_/,'/50_')
var b=''
if(r.buffs && r.buffs.length){
	var x = r.buffs.length>6 ? 6 :r.buffs.length
	for (var i=0; i<x; i++)
		b+="<img src='"+r.buffs[i].url+"' title='"+r.buffs[i].name+':'+r.buffs[i].caption+"' style='width:20px'/> "
	}
y.innerHTML = "<img src='"+r.avatar+"' style='height:35px;border-width:1px;border-style:solid;margin:0px 3px 0px 0px;float:left'/><nobr><a href='http://t.178.com/"+uid+"' class='author' target='_blank'><b>"+r.nickname+"</b></a>"
+(parseInt(r.vip_type,10) ? "<sup class='silver vip"+r.vip_type+"' style='line-height:0.5em'>v</sup>" :'')
+((r.tweet_count || r.fans_count) ? " <span title='他发布的信息/关注他的人数' class='silver'>("+r.tweet_count+"/"+r.fans_count+")</span>" :"")
+(r.fan_url ? " <a href='"+r.fan_url+"' target='_blank title='关注他' class='blue'>尾随</a>" : '')
+"</nobr>"
+(b ? '<br/><nobr>'+b+'</nobr>': '')
+"<div style='margin-bottom:3px;height:5px;line-height:0px;border-bottom:1px solid #F7E1A1;clear:both'></div>"
+y.innerHTML

y.style.marginTop='-'+y.offsetHeight+'px'

}//fe

/*
 * 改变特殊字符的背景色
 * @param y 字符串
 */
commonui.colorChar = function (y){
z=''
for (var i=0; i<y.length; i++){
	var u=y.substr(i,1),x = y.charCodeAt(i)
	if(x>=0x00 && x<=0x7f)z+=u
	else if(x>=0x3400 && x<=0x4DB5)z+=u
	else if(x>=0x4E00 && x<=0x9FA5)z+=u
	else if(x>=0x9FA6 && x<=0x9FBB)z+=u
	else if(x>=0xF900 && x<=0xFA2D)z+=u
	else if(x>=0xFA30 && x<=0xFAD9)z+=u
	else if(x>=0xFA70 && x<=0xFAD9)z+=u
	else z+='<span style="color:limegreen;background:#fcc">'+u+'</span>';
	}
return z
}

//订阅==================

commonui.unionforumSubscribe = function (o,ufid,type){
//if (!__CURRENT_UID)
//	return window.alert('必须登录')
if(typeof(o)=='string')
	o=o.replace(ufid,'').replace(/(^),|,($)|(,),+/,'$1$2$3')
if (!o && type)
	return window.alert('必须选择一个')

if (!__CURRENT_UID){
	if (!window.__UNION_FORUM)
		return window.alert('参数错误')
	var s = cookieFuncs.getCookie('unionForumSelect');
	if(s)s=__NUKE.scDe(s)
	else s={}
	if (!s[ufid])
		s[ufid] = window.__UNION_FORUM_DEFAULT.split(',')
	else
		s[ufid] = s[ufid].split(',')

	if (type==1)
		s[ufid]=s[ufid].concat(o.split(','))
	else if (type==2){
		var x= new RegExp(o.replace(',','|'),'g')
		s[ufid] = s[ufid].join(',').replace(x,'').split(',')
		}
	else
		s[ufid]=o.split(',')

	s[ufid]=s[ufid].sort().join(',').replace(/(^),|,($)|(,),+/,'$1$2$3')

	if(s[ufid]!= window.__UNION_FORUM_DEFAULT.split(',').sort().join(',').replace(/(^),|,($)|(,),+/,'$1$2$3'))
		cookieFuncs.setCookieInSecond('unionForumSelect',__NUKE.scEn(s),3600*24*7);
	else
		cookieFuncs.setMiscCookieInSecond('unionForumSelect',null,0);
	return window.alert('操作成功，刷新页面后将显示选中版面的主题')
	}

if (type==1)
	type = '&add=1'
else if (type==2)
	type = '&del=1'
else
	type=''
httpDataGetter.script_muti_get("/nuke.php?func=save_subscription&ufid="+ufid+'&fid='+o+type,
	function(r){
		window.alert(r.data)
		},
	function(){},
	'gbk'
	)
}//fe


commonui.unionforum_subscribe = function (o,ufid,s){//旧接口兼容
if(typeof(o)=='number')
	commonui.unionforumSubscribe(o,ufid,1)
else if(s){
	var i = o.getElementsByTagName('select')[0]
	if(i && i.options[i.selectedIndex].value)
		commonui.unionforumSubscribe(i.options[i.selectedIndex].value,ufid,1)
	else
		return
	}
else{
	var x = '';
	var i = o.getElementsByTagName('input')
	for (var k in i){
		if (i[k].checked && i[k].value)
			x+=','+i[k].value
		}
	commonui.unionforumSubscribe(x.substr(1),ufid)
	}
}//fe

//翻译==================

commonui.autoTranslate={
from:null,
to:null,
exp:null,
main:function(o,fid){
if (this.loading)
	return
if (this.from){
	this.act(o)
	}
else{
	this.loading=true
	var self = this
	httpDataGetter.script_muti_get('/nuke.php?func=auto_translate&fid='+fid,
		function(x){
			if (!x)
				return false;
			if(x.data){
				self.from = x.data[0]
				self.to = x.data[1]
				self.exp = ''
				for (var k in self.from){
					self.exp+='|'+k.replace(/([\[\]\\?+.{}()*^$])/g,'\\$1')
					if(k.toUpperCase()!=k)self.from[k.toUpperCase()]=self.from[k]
					}
				self.exp = new RegExp(self.exp.substr(1),'ig')
				self.act(o)
				self.loading=false
				return true
				}
			else
				return false
			},
		function(){}
		)
	}
},//fe

act:function(o){
if (typeof(o)=='string')var c = o
else var c=o.innerHTML
var self = this
c = c.replace(/(^|>)([^<]*)(<|$)/g,function ($0,$1,$2,$3){
	$2 = $2.replace(self.exp,function ($0,$i){
		var x = $0.toUpperCase();
		if (self.from[x]==='undefined') return $0
		if (x.match(/^[a-zA-Z_]/) && $2.charAt($i-1).match(/[a-zA-Z_]/)) return $0
		if (x.match(/[a-zA-Z_]$/) && $2.charAt($i+$0.length).match(/[a-zA-Z_]/)) return $0
		if (x.match(/^[0-9]/) && $2.charAt($i-1).match(/[0-9]/)) return $0
		if (x.match(/[0-9]$/) && $2.charAt($i+$0.length).match(/[0-9]/)) return $0
		return '<span class="auto_trans" title="'+self.to[self.from[x]]+'">'+$0+'</span>'
		})
	return $1+$2+$3
	})
if (typeof(o)=='string')return c;
else o.innerHTML=c
}//fe

}//ce

//多版面选择 ==================

commonui.selectForum = {
getInfo:function (){
if(typeof(window.__ALL_FORUM_INFO)=='object')
	return window.__ALL_FORUM_INFO
var x = window.__ALL_FORUM_INFO.split(','), y={}
for (var i=0;i<x.length;i+=2){
	if(x[i] && x[i+1])
		y[x[i]]=x[i+1]
	}
return y
},//fe

getSelect : function (def){
var x = ''
if (window.__SELECTED_FORUM)//用户选择的版面
	x+= __SELECTED_FORUM+','
if (def==1 && window.__UNION_FORUM_DEFAULT)//默认选择的版面
	x+= __UNION_FORUM_DEFAULT
if (def==2 && window.__UNION_FORUM)//默认的版面
	x+= __UNION_FORUM
if (!x)return {forums:{},length:0}

var z = this.getInfo()

var x = x.split(',')
var y = {},j=0
for (var i=0;i<x.length;i++){
	if(x[i] && !y[x[i]]){
		y[x[i]]=z[x[i]]
		j++
		}
	}
return {forums:y,length:j}
},//fe

genHint : function (){
if (!window.__CURRENT_FID || !window.__UNION_FORUM)return false
var y = this.getSelect(),x=''
for (var i in y.forums)
	x +="/ <a href='/thread.php?fid="+i+"' class='silver'>"+y.forums[i]+'</a> ';
	
if(y.length==1)
	return "<span style='font-size:120%' class='red b'>本版可以合并显示其他版面的主题<br/>你可以 <a href='javascript:void(0)' onclick='commonui.selectForum.genSelect(event)'>设置显示你想看到的版面</a></span>"
else
	return "<span style='font-size:120%' class='red b'>本版面合并显示了"+x.substr(1)+"的主题<br/>你可以 <a href='javascript:void(0)' onclick='commonui.selectForum.genSelect(event)'>设置为只显示你想看到的版面</a></span>"
},//fe

genSelect : function (event){
if (!window.__CURRENT_FID || !window.__UNION_FORUM)return false
var y = this.getSelect(2),z = this.getSelect(),x='',a=''
for (var i in y.forums){
	if(z.forums[i]){
		a = "checked='true'"
		if(window.__CURRENT_FID ==i )
			a=" checked='true' disabled='true'"
		}
	else
		a=''
	x +="<input type='checkbox' value='"+i+"' "+a+"> "+y.forums[i]+'<br/>';
	}
commonui.createadminwindow()
var a = commonui.adminwindow
a._.addContent(null)
a._.addContent( 
	_$("<span/>")._.css({fontSize:'120%',fontWeight:'bold'})._.attr('innerHTML',x,1)
	)
a._.addContent( 
	_$("<button/>")._.attr({type:'button'})._.attr('innerHTML','提交',1)._.on('click',function (){
		var tmp = this.parentNode.getElementsByTagName('input'),v=[]
		for (var i=0;i<tmp.length;i++){
			if (tmp[i].checked && !tmp[i].disabled)
				v.push(tmp[i].value)
			}
		commonui.unionforumSubscribe(v.join(','),window.__CURRENT_FID)
		})
	)
a._.addContent( 
	_$("<button/>")._.attr({type:'button'})._.attr('innerHTML','关闭',1)._.on('click',function (){commonui.hideAdminWindow()})
	)
tTip.showdscp(event,a);

}//fe

}//ce

//document.write过滤==============
if(window.__filterWrite)
	commonui.filterWrite = window.__filterWrite
else
	commonui.filterWrite={load:function(){}, unload:function(){} }
//翻页等按钮切换 ==================
commonui.readFuncSwitch=function(o){
if(o.className.indexOf('page_margin')==-1)return
if(o.firstChild && o.firstChild.className.indexOf('page_nav')==-1)return//无翻页时则退出
var y = o.getElementsByTagName('span'),z = []
for(var k=0;k<y.length;k++){
	if(y[k].className.match(/max_page|to_page|to_level|topic_count|perpage/))
		z.push(y[k])
	}
var y=o.offsetHeight, 
	u=_$('<span style="font-family:serif;'+((window.__UA &&__UA[0]==1 && __UA[1]<=6)?'font-size:'+Math.floor(y/3*2)+'px':'')+'">&nbsp;</span>'),//ie6行高无效修正 
	x = _$('</div>')._.cls('urltip urltip2 page_nav')._.css({height:(y-2)+'px',margin:'0 -100% -100% 0',position:'absolute',padding:'0',lineHeight:(y-2)+'px'})
while(y=z.shift())
	x.appendChild(y)
x._.aC(u.cloneNode(true))
if(o.childNodes[1])
	o.insertBefore(x,o.childNodes[1])
else
	o.appendChild(x)
o.className = o.className.replace('page_short','')
commonui.aE(o.firstChild,'mouseover',function(e){
	var x = this.parentNode.childNodes[1];
	x.style.display='inline'
	commonui.cancelBubble(e)
	})
commonui.aE(o,'mouseout',function(e){
	if (!e) var e = window.event;
	var to = e.relatedTarget || e.toElement;
	for (var i=0;i<7;i++){
		if(to){
			if (to==this)
				return
			to=to.parentNode
			}
		}
	to=this.childNodes[1]
	x.style.display='none'
	})
}

//二次点击============================
commonui.doubleclick = {

currentFocus : null,
allElmCount:0,


focus : function(e,o){
if(!o)o=this
var x = commonui.doubleclick
if(x.currentFocus && x.currentFocus!=o)
	x.blur(null,x.currentFocus)
x.currentFocus=o
o._touchSelected=true
o.style.backgroundColor='#faa'
o.style.MozBoxShadow='inset 0 0 3px #f00,0 0 3px #f00';
o.style.WebkitBoxShadow='inset 0 0 3px #f00,0 0 3px #f00';
o.style.boxShadow='inset 0 0 3px #f00,0 0 3px #f00';
},

blur : function (e,o){
if(!o)o=this
o.style.backgroundColor=''
o.style.MozBoxShadow='';
o.style.WebkitBoxShadow='';
o.style.boxShadow='';
o._touchSelected=false
},

ontouchstart : function (e){
var o = this
if(!o._touchSelected){
	o._previousEventTime = e.timeStamp.valueOf()
	commonui.doubleclick.focus(e,this)
	}
},

onclick : function(e){
if (!window.__TOUCH)
	return
var o = this,et = e.timeStamp.valueOf()
if (o._previousEventTime && (et-o._previousEventTime)<1000){
	try{e.stopPropagation();e.preventDefault()}catch(er){}
	e.cancelBubble =true,e.returnValue = false
	return
	}
if(o._touchSelected){
	commonui.doubleclick.blur(e,o)
	}
else{
	commonui.doubleclick.focus(e,o)
	try{e.stopPropagation();e.preventDefault()}catch(er){}
	e.cancelBubble =true,e.returnValue = false
	}
},

init:function(a){
if(a._haveSetTouchHandler)return
_$(a)._.on('click',commonui.doubleclick.onclick)._.on('touchstart',commonui.doubleclick.ontouchstart)
a._haveSetTouchHandler = true
},

onTouchStartInitAll:function(){
var a = document.getElementsByTagName('*')
if(a.length != commonui.doubleclick.allElmCount){
	commonui.doubleclick.allElmCount = a.length
	a = document.getElementsByTagName('a')
	for (var i=0; i<a.length; i++)
		commonui.doubleclick.init(a[i])
	}
}
}//ce

//标准block============================
/*
* @param id node id
* @param name 标题
* @param info 附加介绍
* @param node/ o 内容
*/
commonui.genStdBlock_a =function(id,name,info,o){
var $ = window._$
return $('/span').$0('id',id,
	$('/h2').$0('className','catetitle','innerHTML',':: '+name+' ::',
		__NUKE.trigger(function(){
			if(commonui.customBackgroundCheckHeight && commonui.customBackgroundCheckHeight(this.parentNode))
				this.parentNode.className+=" invertThis"
			})
		),
	$('/div').$0('style',{textAlign:'left',lineHeight:'1.8em'}, 'className','catenew', 'id',id+'Content',
		$('/div').$0('style',{padding:'5px 10px'},
			info ? $('/div').$0('className','gray', 'innerHTML',info) : null,
			o,
			$('/div').$0('className','clear')
			)
		)
	)
}

//nouse ==================

//附加码验证 ==================
commonui.additional_check=function(code){
var z = "设置完毕 验证信息将在COOKIE中保存30天 不生效请点击一次发布新主题";
loader.script(__COMMONRES_PATH+'/js_md5.js',
	(code ?
	function(){
		cookieFuncs.setCookieInSecond("additional_check",hex_md5(code.substr(0,3)+__CURRENT_UID+code.substr(3,3)+__CURRENT_UID+code.substr(6)),86400*30);
		alert(z);
		}
	: function(){
		commonui.createadminwindow()
		var x = commonui.adminwindow,id='grefsar'+Math.random()
		x._.addContent(null)
		x._.addTitle('附加验证(Beta)')
		x._.addContent( _$('<span/>')._.aC(
			"<span>填入附加码<br/><input id='"+id+"' type='text'><br/><button onclick='var tmp = $(\""+id+"\").value;cookieFuncs.setCookieInSecond(\"additional_check\",hex_md5(tmp.substr(0,3)+\""+__CURRENT_UID+"\"+tmp.substr(3,3)+\""+__CURRENT_UID+"\"+tmp.substr(6)),86400*30);alert(\""+z+"\")' type='button'>确定</button><button onclick='cookieFuncs.setCookieInSecond(\"additional_check\",null,0);alert(\"验证信息清除\")' type='button'>清除COOKIE</button><button onclick='commonui.hideAdminWindow()' type='button'>关闭</button></span>")  )
		var s = commonui.getScroll();
		tTip.showdscp({clientX:s.x,clientY:s.y,pageX:s.x,pageY:s.y},x);
		})
	)
}


commonui.dispReadReputation = function(o,uid,fid)
{
if (isNaN(parseInt(uid,10)))return;
if (typeof(fid)=='number' && fid<0) fid='&rid='+fid;
else fid=''
httpDataGetter.script_muti_get("/nuke.php?func=load_user_reputation&uid="+uid+fid,
	function(r){
	if (!r)
		{
		o.innerHTML = 'data error';
		return false;
		}
	else
		{
		var l='';
		var x = '';
		r = r.data;
		for (var k in r)
			{
			l=commonui._r_f.format(r[k]['r'])
			x+='<tr><td>'+r[k]['n']+' <span class=numeric>('+l.value+')</span><div class=r_container><div style=\"width:'+(l.rate*100)+'%\" class=r_bar></div></div><span class=silver>'+l.name+' <span class=numeric>('+l.valueDisp+'/'+l.range+')</span></span></td></tr>';
			}
		if (x)
			x = '<table class=reputation_table cellspacing=0><tbody>'+x+'</tbody></table>';
		else
			x = 'no reputation';
		o.innerHTML = x;
		return true
		}
	},
	function(){
	o.innerHTML = 'get error';
	},
	'gbk'
	)
}//fe