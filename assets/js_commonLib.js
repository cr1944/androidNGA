/*
========================
commonlib v1.0
------------
(c) 2010 Zeg All Rights Reserved
========================
*/

/*
 *基本功能
 */
var __NUKE = {
	
_w : window,
_d : window.document,

//简单的复制一个obj
simpleClone : function (o){
if(o == null || typeof(o) != 'object')
	return o;
var oo = new o.constructor(); // changed (twice)
for(var k in o)
	oo[k] = this.simpleClone(o[k]);
return oo;
},//fe

//继承一个obj
inheritClone : function (o){
if(o == null || typeof(o) != 'object')
	return o;
var oo =function(){};
oo.prototype=o
return new oo;
},//fe

scEn:function (v,no){
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
},//fe

scDe:function (s){
s = s.split('~')
if(s.length==1)return s
var v={}
for (var i=0;i<s.length;i+=2)
	v[s[i]]=s[i+1]
return v
},//fe

getDocSize:function(){
var p = this.position.get()
p.sW = p.pw
p.sH = p.ph
p.cW = p.cw
p.cH = p.ch
p.sL = p.xf
p.sT = p.yf
return p
},//fe


position : {
b:null,
getOffset:null,
get:function(e){

if (!this.b)
	this.b=(!document.compatMode || document.compatMode == 'CSS1Compat') ? document.documentElement : document.body;

if (!this.getOffset){
	if (typeof window.pageYOffset != 'undefined')
		this.getOffset = function(){
			var b = this.b;
			return {xf:window.pageXOffset, yf:window.pageYOffset, pw:b.scrollWidth, ph:b.scrollHeight, cw:b.clientWidth, ch:b.clientHeight} 
			}
	else
		this.getOffset = function(e){
			var b = this.b;
			return {xf:b.scrollLeft,yf:b.scrollTop, pw:b.scrollWidth, ph:b.scrollHeight, cw:b.clientWidth, ch:b.clientHeight}
			}
	}
	
var p = this.getOffset()

if(e){
	if (e.targetTouches)
		e = e.targetTouches[0];

	if(e.pageX===undefined){
		p.x = e.clientX
		p.y = e.clientY
		p.px = p.x+p.xf
		p.py = p.y+p.yf
		}
	else if(p.yf && e.pageY==e.clientY){//mobile safari
		p.x = e.pageX - p.xf
		p.y = e.pageY - p.yf
		p.px = e.pageX
		p.py = e.pageY
		}	
	else{
		p.x = e.clientX
		p.y = e.clientY
		p.px = e.pageX
		p.py = e.pageY		
		}
	}

return p	
	
}//fe
},//ce position

ifMouseLeave:function(e,o){
if (!e) var e = window.event;
var r = e.relatedTarget ? e.relatedTarget : e.toElement,t=r
while (r && r != o && r.nodeName != 'BODY')
	r= r.parentNode
if (r==o) return false;
return t
},//fe
	
toInt:function(n){
var n = parseInt(n,10)
if(!n)n=0
return n
},//fe

trigger:function(f){
return _$('<img/>')._.attr('src','about:blank')._.css('display','none')._.on('error',f)
},//fe


addCss:function(css){
var h = document.getElementsByTagName('head')[0],s = document.createElement('style');
s.type = 'text/css';
if (s.styleSheet){
	h.appendChild(s);
	s.styleSheet.cssText = css;
	}
else {
	s.appendChild(document.createTextNode(css));
	h.appendChild(s);
	}
},//fe


/**
* http request
* @param null a._id 不要设这个
* @param null a._noLock 不要设这个
* @param mix a.u 请求地址 为string时使用get  为{u:url,a:{k1:v1,k2:v2....}}时使用post;  需要使用“多个地址”时[u0,u1 ...]
* @param a.c 返回信息的字符集 不设默认为空（和页面相同编码
* @param a.b 提交按钮/或链接 (可以忽略
* @param a.f 成功时callback 如返回false并且有“多个地址”时则继续尝试下一个
* @param a.ff 失败时时callback 如果地址并全部失败或 a.f全部返回false时执行
* @param a.t 表单的target 如为node则目标iframe会置于node中 如不设使用隐形iframe
* @param a.n 数据变量的名字 不设默认为script_muti_get_var_store (如果存在数据变量会作为callback的第一个参数 
*/
doRequest:function(a){

if(!this.doRequest.form){
	this.doRequest.form = this._w._$('<form/>')._.attr('method','post')._.css('display','none')
	this._d.body.insertBefore(this.doRequest.form,this._d.body.firstChild)

	this.doRequest.target = this._w._$('/span').$0('style',{display:'none'})
	this._d.body.insertBefore(this.doRequest.target,this._d.body.firstChild)
	
	this.doRequest.args=[]
	}
	
if(!a._id)
	a._id = 'doHttpRequest'+Math.floor(Math.random()*10000)
if(!a.n)
	a.n = 'script_muti_get_var_store'
if(!a.t)
	a.t = this.doRequest.target

var f = this._doRequestLock(a)
if(f)return alert(f)

this.doRequest.args.push(a)

if(!this.doRequest.run){
	this.doRequest.run=true
	return window.setTimeout(function(){__NUKE._doRequest()},50)
	}
},//fe

_doRequest:function(){
	
var a = this.doRequest.args[0]

if(!a){
	//console.log('query 0 exit')
	return __NUKE.doRequest.run = false
	}

if(!a.u){
	this._doRequestLock(a,true)
	this.doRequest.args.shift()
	//console.log('query '+this.doRequest.args.length+' continue')
	return window.setTimeout(function(){__NUKE._doRequest()},50)
	}
//console.log('query '+this.doRequest.args.length+' do')
var u = typeof(a.u)=='string' || a.u.u ? a.u : a.u[0]

if(typeof(u)=='string'){
	if(this.doRequest.script)this.doRequest.script.parentNode.removeChild(this.doRequest.script)
	//delete this._w[a.n]
	this._w[a.n] = null
	f = this._w._$('/script')._.attr({id:a._id, src:u, charset:a.c?a.c:'', type:'text/javascript'})._.on('readystatechange',this._doRequestCallback)._.on('load',this._doRequestCallback)._.on('error',this._doRequestCallback)
	this.doRequest.script = f
	this._d.getElementsByTagName('head')[0].appendChild(f)
	return
	}

var f = this.doRequest.form
f.innerHTML=''
f.action = u.u
for(var k in u.a)
	f._.aC(_$('<input/>')._.attr({type:'hidden',value:u.a[k],name:k}))

if(typeof(a.t)=='object' && a.t.nodeType==1){
	f.target = a._id
	a.t.innerHTML = ''
	a.t.appendChild(this._w._$('<iframe/>')._.attr({name:a._id, id:a._id, scrolling:'no', allowtransparency:'true', src:'about:blank', frameBorder:'0'})._.css({width:(a.t.offsetWidth ? a.t.offsetWidth+'px' : '200px'),height:(a.t.offsetHeight ? a.t.offsetHeight+'px' : '50px'),border:'none',overflow:'hidden'}))._.on('readystatechange',this._doRequestCallback)._.on('load',this._doRequestCallback)._.on('error',this._doRequestCallback)
	}
else if(typeof(a.t)=='string')
	f.target = a.t

f.submit()
},

_doRequestLock:function(a,clear){
if(clear){
	if(a.b){
		a.b.disabled = a.b.__submiting = false
		if(a.b.style)
			a.b.style.crusor = ''
		}
	return
	}

if(a.b){
	if(a.b.disabled || a.b.__submiting)
		return '提交中 请稍后再试'
	a.b.disabled = a.b.__submiting = true
	if(a.b.style)
		a.b.style.crusor = 'wait'
	}
},//fe

_doRequestCallback:function(){
var o = this
if((o.readyState && o.readyState!='complete' && o.readyState!='loaded')||o.__loadRunned)
	return
o.__loadRunned = true
var w = window, r = true, a = w.__NUKE.doRequest.args[0]
if(a.f)
	r = a.f.call(w, o.nodeName == 'IFRAME' ? o.contentWindow[a.n] : w[a.n])
if(r || typeof(a.u)=='string' || a.u.u || a.u.length==1){//如成功 或 单get 或 单post 或 队列get或post并且队列中没有下一个了 的时候 结束任务
	if(!r && a.ff)
		a.ff.call(w)
	a.u=null
	}
else
	a.u.shift()
window.setTimeout(function(){__NUKE._doRequest()},50)
},//fe

/**
* http post
* 同doRequest
*/
doPost : function(a){
if(a.a){
	a.u={u:a.u,a:a.a}
	delete a.a
	}
return this.doRequest(a)
}

/*
*表单提交
*@param mix a / a.u 地址  a.a 参数  a.t 表单目标iframe的容器  a.b 提交按钮  a.f 成功时callback a.n数据变量的名字(如果有的话会作为callback的第一个参数

doPost:function(a){
if(!this.doPostForm){
	this.doPostForm = _$('<form/>')._.attr('method','post')._.css('display','none')
	document.body.appendChild(this.doPostForm)
	}
var f = this.doPostForm
if((a.b && a.b.disabled) || (f && f._submiting))
	return alert('提交中 请稍后再试')
a.b.disabled=f._submiting=true
f.innerHTML=''
f.action = a.u
if(!a.n)a.n = 'script_muti_get_var_store'
if(!a.t){
	if(!this.doPost._target){
		this.doPost._target = _$('/span').$0('style',{display:'none'})
		document.body.appendChild(this.doPost._target)
		}
	a.t = this.doPost._target
	}
if(typeof(a.t)=='object' && a.t.nodeType==1){
	f.target = 'dopostform'+Math.floor(Math.random()*10000)
	var onload = function(){
		if((this.readyState && this.readyState!='complete')||this._loadRunned)return
		this._loadRunned = true
		a.b.disabled = false
		f._submiting=false
		if(a.f)a.f.call(this,this.contentWindow[a.n])}
	a.t.innerHTML = ''
	a.t.appendChild(_$('<iframe/>')._.attr({name:f.target,id:f.target,scrolling:'no',allowtransparency:'true',src:'about:blank',frameBorder:'0'})._.css({width:(a.t.offsetWidth ? a.t.offsetWidth+'px' : '200px'),height:(a.t.offsetHeight ? a.t.offsetHeight+'px' : '50px'),border:'none',overflow:'hidden'}))._.on('readystatechange',onload)._.on('load',onload)
	}
else if(typeof(a.t)=='string')
	f.target = a.t

for(var k in a.a)
	f._.aC(_$('<input/>')._.attr({type:'hidden',value:a.a[k],name:k}))
f.submit()
},//fe
doPostForm:null

*/

}//ce

//==================================

//DOM

//==================================

window.$ = function(id){return document.getElementById(id)}

window.put = function(txt){document.write(txt)}

/*
*====================================
Element 原型扩展=====================
*/
var domExtPrototype={ 
/*
 *增加样式class
 */
cls:function(cn){
	this.self.className += ' '+cn
	return this.self 
	},
/*
 *设定css样式
 *@param name , value , name , value , name , value ...
 *@param (obj)o/{name:value,name:value,name:value...}
 */
css:function(){
	if(arguments.length==1){
		var o = arguments[0]
		for (var k in o)
			this.self.style[k]=o[k]
		}
	else
		for(var i=0;i<arguments.length;i+=2)
			this.self.style[arguments[i]]=arguments[i+1]
	return this.self
	},
/*
 *绑定事件
 *@param 事件名(无on) , callback(第一个参数是event)
 */
on:function(type, fn){
	if (window.addEventListener)
		this.self.addEventListener(type, fn, false); 
	else if (window.attachEvent){
		var o = this.self
		o.attachEvent('on'+type, function(){fn.call(o, window.event)} ) 
		}
	return this.self; 
	},

/*
 *增加子节点
 *@param node , node , node , node , node , node ...
 *node为null时忽略
 *node为string时node=$(node)
 *node为array时 insertBefore(node.0,node.1)
 */
aC:function(){
	var o = this.self, i=0, a=arguments
	for (;i<a.length;i++){
		if(a[i]===null)continue
		if(a[i].constructor==Array){
			if(typeof(a[i][0])=='string')a[i][0]=$(a[i][0])
			o.insertBefore(a[i][0],a[i][1])
			}
		else{
			if(typeof(a[i])=='string')a[i]=$(a[i])
			o.appendChild(a[i])
			}
		}
	return o;
	},
/*
 *保存数据
 *@param name , value
 */
sV:function (o,v){
	if(!this.anyVar)this.anyVar={}
	if (v!==undefined) 
		this.anyVar[o]=v
	else 
		for (var k in o)  
			this.sV(k, o[k]) 
	return this.self
	},
/*
 *取出数据
 *@param name
 */
gV:function (k){
	if(!this.anyVar)this.anyVar={}
	return this.anyVar[k] 
	},
/*
 *增加子节点
 *@param node , node , node , node , node , node ...
 *node为null时忽略
 *node为string时node=document.createTextNode(node)
 *node为array时insertBefore(node.0,node.1)
 */
add:function(){
	var o = this.self, i=0, a=arguments
	for (;i<a.length;i++){
		if(a[i]===null)continue
		if(a[i].constructor==Array){
			if(typeof(a[i][0])=='string')a[i][0]=document.createTextNode(a[i][0])
			o.insertBefore(a[i][0] , a[i][1])
			}
		else{
			if(typeof(a[i])=='string')a[i]=document.createTextNode(a[i])
			o.appendChild(a[i])
			}
		}
	return o
	},
/*
 *综合调用
 *@param attrName , attrValue , attrName , attrValue ... 设置属性 具体参看attr函数
 *@param {name: value, name: value ...} 批量设置属性
 *@param event , callback , event , callback ... 注册事件 具体参看on函数
 *@param domNode , domNode ...  增加子节点 (有nodeType属性的object视为domNode) null参数会被跳过 具体参看add函数
 *以上参数可混用
 *设定className属性时如value为null则清空
 *设定style属性时value为{cssName: value, cssName: value ...}
 */
call:function(){
	for(var i=0;i<arguments.length;++i){
		var a = arguments[i]
		if(typeof a == 'object'){
			if(a===null)
				continue
			else if(a.nodeType)
				this.self.appendChild(a)
			else
				for(var k in a)
					this.attr(k,a[k])
			}
		else
			this.attr(a,arguments[++i])
		}
	return this.self
	},
/*
 *设定属性或样式或事件注册
 *@param name , value 
 *@param event , callback 
 *@param {name: value, name: value ...}
 *设定className时如value为null则清空
 *设定style时value为{cssName: value, cssName: value ...}
 */
attr:function(k,v){
	if(typeof v == 'function' && k.substr(0,2)=='on')
		return this.on(k.substr(2),v)
	if(arguments.length==1 && typeof k =='object'){
		for(var i in k)
			this.attr(i,k[i])
		return this.self
		}
	var o = this.self
	switch(k){
		case 'className':
			if(v===null)
				o.className=null
			else if(v)
				o.className+=' '+v
			break
		case 'style':
			if(typeof v=='string'){
				v = v.split(/:|;/)
				for(var s=0;s<v.length;++s)
					o.style[v[s]]=v[++s]
				}
			else
				for(var s in v)
					o.style[s]=v[s]
			break
		case 'id':
		case 'innerHTML':
		case 'title':
		case 'checked':
		case 'accessKey':
		case 'dir':
		case 'disabled':
		case 'lang':
		case 'tabIndex':
		case 'width':
		case 'height':
			o[k]=v
			break
		default:
			o.setAttribute(k,v)
		}
	return o
	}
}//oe

/*
//新建元素 
var x = $('<span>')
var x = $('<span/>')
var x = $('/span')
var x = $('<span>abcd</span>')
//用id取元素 
var x = $('xxoo')
//链式调用  综合调用 (参看domExtPrototype.call)
$('xxoo')._.cls('xxxxoooo')._.attr('title','abcd')._.add($('<span/>'),$('<div/>'))
$('xxoo').$0('className','xxoo').$0('style',{width:'100%'},$('/span'),'onclick',function(e){alert(123)})
$('xxoo').$0('className','xxoo','style',{width:'100%'},$('/span'),'onclick',function(e){alert(123)})
*/
window._$ = function (o){//fs
if(typeof o == 'string'){
	var x = o.substr(0,1)
	if(x=='/' || x=='<'){
		if(x = o.match(/^<?\/?([a-zA-Z0-9]+)\/?>?$/))
			o = document.createElement(x[1])
		else{
			x = document.createElement('span')
			x.innerHTML = o
			o = x.firstChild
			}
		}
	else
		o = document.getElementById(o)
	}
if(o!==null && o._==null){
	o._=function(){}
	o._.prototype=domExtPrototype
	o._ = new o._
	o._.self = o
	if(!('$0' in o))//ie6 ie7
		o.$0=function(){return this._.call.apply(this._,arguments)}
	}
return o
}//fe 

/*
*为element原型增加综合调用函数$0 ie6 ie7之外
*/
if (window.Element)
    window.Element.prototype.$0=function(){return this._.call.apply(this._,arguments)}



//==================================

//XMLHttpRequest

//==================================
/*
var HTTP = (function()
{
var xmlhttp = false,e1,e2,e3;
try{
	xmlhttp = new ActiveXObject("Msxml2.XMLHTTP");
	}
catch(e1)
	{
	try{
		xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
		}
	catch(e2){
		xmlhttp = false;
		}
	}
if(!xmlhttp && typeof XMLHttpRequest!="undefined")
	{
		try{
			xmlhttp = new XMLHttpRequest();
			}
		catch (e3){
			xmlhttp = false;
			}
	}
//if (!xmlhttp)window.alert(e1 + e2 + e3);
return xmlhttp;
})();
*/
//==================================

//AJAX & SJAX

//==================================
var httpDataGetter={script_muti_get:function(u,h,hf,c,cN){
var a = {
	u:u,
	f:h,
	ff:hf,
	c:(typeof(c)=='object' ? c.charset : c),
	n:(cN ? cN : typeof(c)=='object' ? c.varName : null) 
	}
return __NUKE.doRequest(a)
}
}
/*
var script_muti_get_var_store = null
var httpDataGetter = {

script_muti_syncget : function(url,f){
if (typeof(url)=='string')
	url=[url]
var u
while (u = url.shift()){
	HTTP.abort();
	HTTP.open('GET', u, false)
	HTTP.send('');
	if (HTTP.status==404)continue;
	u = HTTP.responseText
	if(u.indexOf('window.script_muti_get_var_store')!=-1){
		u = u.replace('window.script_muti_get_var_store','var u')
		eval(u)
		}
	u = f(u)
	if(u===false)continue;
	return u
	}
return undefined
},
//fe

script_muti_get_set_costom_value:function(v){
this._SMG.setCustomVar(v)
},//

script_muti_get:function(u,h,hf,c,cN)
{
this._SMG.get(u,h,hf,c,cN)
},//



_SMG:{
	getting:false,
	waiting:false,
	queue:[],
	cache:{},
	script:null,

	clone:function(o){ return __NUKE.simpleClone(o) },//fe

	setCustomVar:function (v){window.script_muti_get_var_store = v;},//fe

	//u: url array
	//h: success handler, return bool true to exit, return bool false to try next url
	//hf: all fail(no url) handler
	//c: script charset
	get:function(u,h,hf,c,cN){
		if (typeof(u)=='string')  u = [u];
		if (!c)c={}
		else if (typeof(c)=='string') c={'charset':c};
		if (cN)c.varName=cN
		this.queue.push({u:u,h:h,hf:hf,c:c})
		var self = this;
		window.setTimeout(function(){self.loop()},0)
		},//fe

	loop:function (noCheck){
		if(!noCheck){
			if (this.getting)
				return;
			}
		if(!this.queue.length){
			this.getting = false
			return;
			}
		else
			this.getting=true
		var u= this.queue.shift()
		this.act(u)
		},//fe

	act:function(u){
		var self=this
		var handler = function(){
			if (this.readyState && this.readyState != 'loaded' && this.readyState != 'complete')
				return
			if (u.c.varName)
				window.script_muti_get_var_store = window[u.c.varName];
			if (u.h(window.script_muti_get_var_store)){
				//if(!u.c.noCache)self.cache[this.src.toLowerCase()]=self.clone(window.script_muti_get_var_store)
				self.waiting = window.setTimeout(function(){self.loop(1)},0)
				return true
				}
			u.u.shift();
			if (u.u.length)
				self.waiting = window.setTimeout(function(){self.act(u)},0)
			else{
				u.hf();
				this.waiting = window.setTimeout(function(){self.loop(1)},0)
				}
			}
		//if(!u.c.noCache){
		//	if (u.u[0].indexOf('http://')==-1) var k = window.location.href.replace(/(http:\/\/.+?)(\/|$).* /,'$1')+u.u[0];
		//	else var k = u.u[0];
		//	if (this.cache[k.toLowerCase()]){
		//		u.h(this.cache[k.toLowerCase()]);
		//		this.waiting = window.setTimeout(function(){self.loop(1)},0)
		//		return;
		//		}
		//	}
		var h = document.getElementsByTagName('head')[0]
		if (this.script)
			h.removeChild(this.script)
		var s = document.createElement('script');
		if (u.c.charset) s.charset = u.c.charset;
		s.type = 'text/javascript'
		if(s.readyState)
			s.onerror = s.onreadystatechange = handler
		else
			s.onerror = s.onload = handler
		window.script_muti_get_var_store = null;
		if(u.c.varName)window[u.c.varName]=null
		s.src= u.u[0];
		this.script=s
		h.insertBefore(s,h.firstChild)
		}//fe
	}//ce




}//ce
*/
//==================================

//DOM STORE

//==================================

if (window.ActiveXObject || window.globalStorage || window.localStorage)
{
var domStorageFuncs = {
now:null,
domain:null,
o:null,
type:null,
init : function(v){
if(v)this.domain=v
else this.domain = window.location.hostname
if(window.localStorage){
	this.type=1
	this.o=window.localStorage
	}
else if(window.globalStorage){
	this.type=1
	this.o=window.globalStorage[this.domain]
	}
else if (window.ActiveXObject){
	document.documentElement.addBehavior("#default#userdata")
	this.o=document.documentElement
	}
else{
	//window.domStorageFuncs=null
	return
	}
if (window.__NOW)
	this.now=parseInt(__NOW,10)
else{
	this.now=new Date
	this.now=Math.floor(this.now.getTime()/1000)
	}
return true
},
set : function(key, value, timeout) {
if (!this.o && !this.init())return
if(!timeout)timeout = 86400*30
if(this.type==1){
	if(Math.random()<0.1)this.checkTimeout(this.o)
	this.o.setItem(key, (this.now+timeout)+''+value);
	}
else{
	with(this.o){
		try{
			load(key);
			}
		catch (ex){}
		setAttribute("js", value);
		expires = new Date((this.now+timeout)*1000).toUTCString();
		save(key);
		}
	}
},
checkTimeout:function (x){
for (var i=0;i<x.length;x++){
	var y = parseInt(x.getItem(x.key(i)).substr(0,10),10)
	if (!y || (y && y<this.now))
		x.removeItem(x.key(i))
	}
},//fe
get : function(key) {
if (!this.o && !this.init())return
if (this.type==1){
	try{
		var x =this.o.getItem(key)
		}
	catch(ex){
		return null
		}
	if(x){
		var y = parseInt(x.substr(0,10),10)
		if(y){
			if (y>this.now)
				return x.substr(10)
			else{
				this.o.removeItem(key)
				return null
				}
			}
		else
			return x
		}
	}
else{
	with(this.o){
		try{
				load(key);
				return getAttribute("js");
			}
		catch (ex){
				return null;
			}
		}
	}
return null;
},
remove : function(key) {
if (!this.o && !this.init())return
if (this.type==1){
	return this.o.removeItem(key);
	}
else {
	with(this.o){
		try{
			load(key);
			expires = new Date(315532799000).toUTCString();
			save(key);
			}
		catch (ex){};
		}
	}
}
}//end domStorage

}//end if

//==================================

//cookieAndSerialize

//==================================

var cookieAndSerialize = function (domain,path,misccookiename){
cookieFuncs.init(domain,path,misccookiename)
return cookieFuncs;
}//fe forward compatible for nga

var cookieFuncs = {
cookieCache:{},
domain:'',
path:'',
date:null,
now:0,
misccookiename:'',

init:function (domain,path,misccookiename){
if (domain)
	this.domain = domain;
else
	this.domain = window.location.href.toLowerCase().replace(/^http:\/\//,'').replace(/(\/|:).*/,'').replace(/^[^\.]+\.([^\.]+\.)/,'$1');
this.path = path;
this.date = new Date;
this.now = this.date.getTime();
this.misccookiename = misccookiename
},//fe

setCookieInSecond:function (name,value,sec)
{
this.date.setTime(this.now + sec*1000);
document.cookie = name + "="+ escape (value) + ";domain="+this.domain+";path="+this.path+";expires=" + this.date.toUTCString();
},//fe

setMiscCookieInSecond:function (name,value,sec)
{
this.extractMiscCookie()
if(sec===undefined && this.cookieCache[this.misccookiename][name]){
	this.cookieCache[this.misccookiename][name].v = value	
	}
else if (sec>0){
	this.date.setTime(this.now + sec*1000);
	this.cookieCache[this.misccookiename][name] = {
		v:value,
		t:this.date.toUTCString()
		};
	}
else
	delete this.cookieCache[this.misccookiename][name];
this.setCookieInSecond(this.misccookiename,this.json_encode(this.cookieCache[this.misccookiename]),31536000);
},//fe

extractMiscCookie:function(){
var c = this.cookieCache
var n = this.misccookiename;
if (typeof(c[n]) != 'object')
	{
		this.getCookie(n);
		if (typeof(c[n])=='string'){
			if(c[n].charAt(0)=='{'){
				var tmp = {}
				try{eval('var tmp='+c[n]+';');}catch(e){}
				c[n] = tmp;
				}
			else
				c[n]={}
			}
		else
			c[n]={}
	}
},//fe

getMiscCookie:function (name)
{
this.extractMiscCookie();
if (this.cookieCache[this.misccookiename][name] && (Date.parse(this.cookieCache[this.misccookiename][name]['t'])>=this.now) )
	return this.cookieCache[this.misccookiename][name]['v'];
else
	{
		if (this.cookieCache[this.misccookiename][name])
			{
				delete this.cookieCache[this.misccookiename][name];
				this.setCookieInSecond(this.misccookiename,this.json_encode(this.cookieCache[this.misccookiename]),31536000);
			}
		return null;
	}
},//fe

getCookie:function (name){
if (typeof(this.cookieCache[name])=='undefined')
	{
		var arr = document.cookie.match(new RegExp("(^| )"+name+"=([^;]*)(;|$)"));
		if (arr)
			{
				this.cookieCache[name] = unescape(arr[2]);
			}
		else
			{
				this.cookieCache[name] = null;
			}
	}
return this.cookieCache[name];
},//fe

ifMiscCookie:function (){
if (typeof(this._ifMiscCookie)=='boolean') return this._ifMiscCookie;
else if (document.cookie.match(new RegExp("(?:^| )"+this.misccookiename+"="))) this._ifMiscCookie = true;
else this._ifMiscCookie = false;
return this._ifMiscCookie;
},//fe

json_encode:function(v) { 
switch (typeof(v)) { 
	case 'string':
		return '"' + v.replace('"','\\"') + '"';
	case 'number':
		return v.toString(10);
	case 'boolean':
		return v.toString();
	case 'object':
		if(v==null)return 'null'
		var buf = []
		if (v.constructor==Array){
			for (var i=0;i<v.length;i++)
				buf.push(this.json_encode(v[i]));
			return '[' + buf.join(',') + ']';
			}
		else{
			for (var k in v){
				if (parseInt(k))
					buf.push(k + ':' + this.json_encode(v[k]));
				else
					buf.push('"'+k.replace('"','\\"') + '":' + this.json_encode(v[k]));
				}
			return '{' + buf.join(',') + '}';
			}
	default: 
		return 'null';
	}
},//fe


json_decode:function(txt){
try{
	eval('var x = '+txt)
	}
catch (e){
	var x = null
	}
return x;
}//fe
}//ce

//==================================

//Image / style lazy loader

//==================================

var loader = {
ver:4,
w_i: function(s,o){
o.onload = null;
window.setTimeout(function(){o.src=s},100);
},//fe

w_s:function(s,o,writeSelf){
if (!writeSelf)
	o = o.parentNode;
if (s.indexOf(':')==-1){
	if (s.charAt(0)==' ')
		window.setTimeout(function(){o.className=o.className+s},100);
	else
		window.setTimeout(function(){o.className=s},100);
	}
else{
	if (s.charAt(0)==';')
		window.setTimeout(function(){o.style.cssText=o.style.cssText+s},100);
	else
		window.setTimeout(function(){o.style.cssText=s},100);
	}
},//fe

css:function (src,sync){
if(sync){
	sync = "<link rel='stylesheet' href='"+src+"' type='text/css'/>"
	if(document._documentWirteBak)document._documentWirteBak(sync)
	else document.write(sync)
	return
	}
var x = document.createElement('link')
x.href = src
x.rel = 'stylesheet'
x.type = 'text/css'
var h = document.getElementsByTagName('head')[0]
h.insertBefore(x,h.firstChild)
},//fe

scriptTpl:null,
script:function (src,callback,charset,sync){
if(typeof(src)=='object'){
	callback = src[1]
	charset = src[2]
	sync = src[3]
	src = src[0]
	}	
if(!this.scriptTpl)this.scriptTpl = document.createElement('script')
if(sync){
	if(callback){
		var k='call'+Math.random().toString().substr(2)
		this.callback[k]=callback
		if(this.scriptTpl.readyState)
			var c=" onreadystatechange='if(this.readyState && this.readyState != \"loaded\" && this.readyState != \"complete\")return;window.loader.callback."+k+".call(this)' "
		else
			var c=" onload='window.loader.callback."+k+".call(this)' "
		}
	else
		c=''
	sync = "<scr"+"ipt src='"+src+"' "+(charset ? "charset='"+charset+"'" : '')+" "+c+" type='text/javasc"+"ript'></scr"+"ipt>"
	if(document._documentWirteBak)document._documentWirteBak(sync)
	else document.write(sync)
	return
	}
var x = this.scriptTpl.cloneNode(0)
x.src=src
if(charset)x.charset = charset
if (callback) {
	if(x.readyState){
		x.onreadystatechange = function() {
			if (this.readyState && this.readyState != 'loaded' && this.readyState != 'complete')return;
			callback.call(this);
			}
		}
	else{
		x.onload = function() {callback.call(this)}
		}
	}

var h = document.getElementsByTagName('head')[0]
h.insertBefore(x,h.firstChild)
//commonui._debug.push('start '+src)
},

callback:{}
}//ce

//==================================

//Forward compatible for nga

//==================================
if (!__IMG_BASE)
{
var __AJAX_DOMAIN = window.location.href.toLowerCase().replace(/^http:\/\//,'').replace(/(\/|:).*/,'').replace(/^[^\.]+\.([^\.]+\.)/,'$1');
var __IMG_BASE = 'http://img.'+__AJAX_DOMAIN;
var __CKDOMAIN = '.'+__AJAX_DOMAIN;
}

var w_i = loader.w_i, w_s = loader.w_s, id2e = $