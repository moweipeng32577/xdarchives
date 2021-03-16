/*
 Author：wjh
 Date：2018.05.28
 Version：desktop 1.0
 */
;(function(){
	desktop = {
		menusUrl:'/getlist',//菜单请求链接
		menus:null,//菜单数据
		menuCursor:6,//菜单显示标记位
		frameZindex:1,//窗口最大层级
        splitScreen:false//分屏标识
	};
	
	//初始化函数
	function init(){
		changeContent();//动态改变内容区域高度
		initMenus();//初始化菜单	
	}
	
	function initMenus(){//获取菜单数据
		ajax({
		  	type:"GET", 
		  	url:desktop.menusUrl,
		  	dataType:"json",
		  	success:successFn,
		  	error:function(msg){ 
		    	console.error(msg) 
		  	}
		});

        if(!personalizedObject.path) {
            findElements('.xduserdiv img')[0].setAttribute('src','img/user_default.png');
        }else{
            findElements('.xduserdiv img')[0].setAttribute('src',imgpath);
        }
        findElements('.xduserdiv img')[0].style.cssText = 'border-radius:50%';

		function successFn(msg){
			try{
				desktop.menus = msg; 
		    	var menusOneHtml = '';
		    	var menusTwos = [];
                if(typeof desktop.menus == 'string'){
                    desktop.menus = JSON.parse(desktop.menus);//IE浏览器下返回数据处理
				}
		    	desktop.menus.forEach(function(item,index,array){
		    		if(item.isp=='1'){
		    			var style = 'style="display:block"';
		    			if(index>5){
		    				style = 'style="display:none"';
		    			}
		    			menusOneHtml += '<li '+style+' id="'+item.tkey+'" class="menugn ianimRight" dataurl="'+item.url+'" isp="'+item.isp+'" datacode="'+item.code+'"><img src="img/icon/'+item.icon+'"><br/>'+item.name+'<div class="iselect layui-anim layui-anim-fadein" style="display: none;"></div></li>';
		    		}else{
		    			menusTwos.push(item);
		    		}
		    	});

		    	findElements('.xdmenusdiv>ul')[0].innerHTML = menusOneHtml;
                if(findElements('.xdmenusdiv>ul>li').length<=6){
                    findElements('.menurightgif')[0].style.cssText = 'visibility:hidden;opacity: 0;';
                }

		    	menusTwos.forEach(function(item,index,array){
		    		var iselecttext = document.createElement('div');//创建子菜单
		    		iselecttext.setAttribute('class','iselecttext menugn');//设置相关属性
		    		iselecttext.setAttribute('dataurl',item.url);//设置点击url
					iselecttext.setAttribute('datacode',item.code);
		    		iselecttext.setAttribute('id',item.tkey);
                    iselecttext.setAttribute('isp',item.isp);
		    		var img = document.createElement('img');//图标
		    		img.setAttribute('src','img/icon/'+item.icon);
		    		var span = document.createElement('span');//文本
		    		span.appendChild(img);
		    		span.appendChild(document.createTextNode(' '+item.name));
		    		iselecttext.appendChild(span);
		    		findElements('#'+item.isp+' .iselect')[0].appendChild(iselecttext);
		    	});
		    	
		    	//菜单鼠标悬停事件-进入
				eventBind('mouseenter','.xdmenusdiv>ul>li',function(e){
					var ele = e.srcElement || e.target
						,eleSub = ele.querySelector('.iselect');
						if(eleSub){
							eleSub.style.cssText = 'display:block';
						}
				});
				
				//菜单鼠标悬停事件-出去
				eventBind('mouseleave','.xdmenusdiv>ul>li',function(e){
					var ele = e.srcElement || e.target
						,eleSub = ele.querySelector('.iselect');
						if(eleSub){
							eleSub.style.cssText = 'display:none';
						}
				});

                //用户鼠标悬停事件-进入
                eventBind('mouseenter','.xduserdiv',function(e){
                    var ele = e.srcElement || e.target
                        ,eleSub = ele.querySelector('.iselect1');
                    if(eleSub){
                        eleSub.style.cssText = 'display:block';
                    }
                });

                //用户鼠标悬停事件-出去
                eventBind('mouseleave','.xduserdiv',function(e){
                    var ele = e.srcElement || e.target
                        ,eleSub = ele.querySelector('.iselect1');
                    if(eleSub){
                        eleSub.style.cssText = 'display:none';
                    }
                });
		    	
		    	//菜单点击事件
		    	eventBind('mousedown','.menugn',function(e){
		    		e.cancelBubble = true;//禁止事件冒泡
		    		var ele = e.srcElement || e.target;
                    ele = this;
                    document.oncontextmenu=new Function("event.returnValue=false;");//禁止鼠标右击默认事件
                    if(ele.tagName=='LI'&&ele.lastChild.childNodes.length>0){//排除有子菜单的点击
                        return;
                    }
					if(e.button==0){
                        menuClick(ele);
					}else if(e.button==2){
						var tbcount = findElements('.xdframeHome>iframe')[0].contentWindow.tbcount;
                        var imgUrl = (ele.firstChild.tagName!='IMG'?ele.firstChild.firstChild:ele.firstChild).getAttribute('src')//图标
                            ,clickUrl = ele.getAttribute('dataurl')//点击链接
                            ,tptext = ele.innerText.trim()//任务栏文本
                            ,moduleId = ele.getAttribute('id')//标识
							,code = ele.getAttribute('datacode')//标识
							,shortcuts = [];

						if(ele.tagName!='LI'){
							shortcuts.push({
                                id: moduleId.substring(1),
                                pid: 0,
                                code: code,
                                tkey: moduleId,
                                url: clickUrl,
                                icon: imgUrl,
                                text: tptext,
                                orders:tbcount+""
                            });
						}

						shortcuts.push({
                            id: moduleId.substring(1),
                            pid: code.substring(1),
                            code: code,
                            tkey: moduleId,
                            url: clickUrl,
                            icon: imgUrl,
                            text: tptext,
                            orders:tbcount+""
						});


                    }

					findElements('.xdframeHome>iframe')[0].contentWindow.saveIcon(shortcuts);
		    	});
		    	
		    	function menuClick(ele){
		    		if(ele){
		    			var imgUrl = (ele.firstChild.tagName!='IMG'?ele.firstChild.firstChild:ele.firstChild).getAttribute('src')//图标
							,clickUrl = ele.getAttribute('dataurl')//点击链接
							,tptext = ele.innerText.replace("-","").trim()//任务栏文本
							,moduleId = ele.getAttribute('id')//标识
							,navEle = findElements('.xdnav')[0]//导航对象
							,contentEle = findElements('.xdcontent')[0]//框架主体对象
							,nowFrameEle = findElements('#'+moduleId+'Frame')[0]
							,navtpEles = findElements('.navtp')
							,navtp;
		    			if(nowFrameEle){
		    				nowFrameEle.style.cssText = 'z-index:'+desktop.frameZindex;
		    			}else{
		    				//生成任务
							navtp = document.createElement('div');
							navtp.setAttribute('class','navtp layui-anim layui-anim-scaleSpring');
							navtp.setAttribute('id','nav'+moduleId+'Frame');
							navtp.innerHTML = '<img class="navtpimg" src="'+imgUrl+'"/> '+tptext+' <span class="navtpclose"><img src="img/close.png"/></span>';
							navEle.appendChild(navtp);

		    				//生成页面
		    				var frametp = document.createElement('div');
		    				frametp.setAttribute('class','xdframe');
		    				frametp.setAttribute('id',moduleId+'Frame');
		    				frametp.style.cssText = 'z-index:'+desktop.frameZindex;
		    				frametp.innerHTML = '<div class="iloading">' +
								'<div class="spinner"><div class="rect1"></div><div class="rect2"></div><div class="rect3"></div><div class="rect4"></div><div class="rect5"></div></div>' +
								'</div><iframe src="'+clickUrl+'" style="width:100%;height: 100%;"></iframe>';
		    				contentEle.appendChild(frametp);

		    				eventBind('load','#'+moduleId+'Frame iframe',function(){
		    					findElements('#'+moduleId+'Frame .iloading')[0].style.cssText = 'display: none;';
		    				});
		    				
		    				eventBind('click','.navtp',navtpEvent);
		    				eventBind('click','.navtpclose',navcloseEvent);
		    			}
		    			
		    			for(var i=0;i<navtpEles.length;i++){
							navtpEles[i].style.cssText = 'background: rgba(52,152,213,1);';
						}
		    			
		    			if(!navtp){
		    				navtp = findElements('#nav'+moduleId+'Frame')[0];
		    			}

						if(ele.firstChild.tagName!='IMG'){
							ele.parentElement.style.cssText = 'display:none';
						}

						navtp.style.cssText = 'background: rgba(52,152,213,.5);';
						desktop.frameZindex++;
		    		}
		    	}

                desktop.menuClick = menuClick;
			}catch(e){
				console.error(e);
			}
			
		}
		
		//任务栏点击事件
		function navtpEvent(e){
			try{
				var ele = e.srcElement || e.target
					,navtpEles = findElements('.navtp')
					,splitScreen = findElements('.splitScreen')[0]
					,style = 'z-index:'+desktop.frameZindex+';';
				if(ele.tagName!='DIV'){
					ele = ele.parentElement;
				}
				
				for(var i=0;i<navtpEles.length;i++){
					navtpEles[i].style.cssText = 'background: rgba(52,152,213,1);';
				}
				ele.style.cssText = 'background: rgba(52,152,213,.5);';
                if(splitScreen.getAttribute('datastate')=='1'){
                    style += 'width:50%;border:2px solid #000 ;';
                    if(desktop.splitScreen){
						style += 'left:50%;';
                        desktop.splitScreen = false;
					}else{
                        desktop.splitScreen = true;
                    }
                    //findElements('#'+(ele.getAttribute('id').substring(3))+' iframe')[0].style.cssText = 'width:100%;height:100%;';
				}
				findElements('#'+(ele.getAttribute('id').substring(3)))[0].style.cssText = style;
				desktop.frameZindex++;
			}catch(e){
				console.log(e);
			}
			
		}

        //任务栏关闭事件
        function navcloseEvent(e){
            e.cancelBubble = true;//禁止事件冒泡
            var ele = e.srcElement || e.target
                ,navtpId
                ,frameTpId;
            if(ele.tagName!='SPAN'){
                ele = ele.parentElement;
            }
            navtpId = ele.parentElement.getAttribute('id');
            frameTpId = navtpId.substring(3);
            console.log(findElements('#'+navtpId)[0].remove);
            if(findElements('#'+navtpId)[0].remove){
                findElements('#'+navtpId)[0].remove();//删除任务栏
                findElements('#'+frameTpId)[0].remove();//删除框架
			}else{//IE浏览器兼容问题
                findElements('#'+navtpId)[0].parentElement.removeChild(findElements('#'+navtpId)[0]);
                findElements('#'+frameTpId)[0].parentElement.removeChild(findElements('#'+frameTpId)[0]);
			}

        }

        //修改用户信息事件
        eventBind('click','.virtualDesktop',function(e){
        	location.href = "/switchDesktop?tag=1"
		});

        eventBind('click','.manual',function(e){
            window.open('/doc/使用手册.pdf');
        });

        //修改用户信息事件
        eventBind('click','.changepwd',function(e){
            try{
                var index = layer.open({
                    title: '用户信息',
                    type: 2,
                    skin: 'layui-layer-lan',
                    shade: 0,
                    offset: '100px',
                    content: '/user/userMsg',
                    zIndex: layer.zIndex,
                    area: ['370px',  '340px'],
                    shade: 0.3,
                    resize: false,
                    success: function (layero, index) {
                        if (layer.getChildFrame('.logotop').length > 0) {
                            setTimeout(function(){
                                location.href = "/index";
                            },500);
                        }
                    }
                });
            }catch (error){
                console.error(error)
            }
        });

        //弹出退出框事件
        eventBind('click','.logout',function(e){
            try{
                findElements('.xdlogout')[0].style.display = 'block';
            }catch (error){
                console.error(error)
            }
        });

        //退出事件
        eventBind('click','.xdlogoutpaneldown',function(e){
            try{
            	e.cancelBubble = true;
               location.href = '/logoutt';
            }catch (error){
                console.error(error)
            }
        });

        eventBind('click','.xdmsg',function(){
            try{
            	var navtpEles = findElements('.navtp');
                findElements('.xdframeHome')[0].style.zIndex = desktop.frameZindex;
                for(var i=0;i<navtpEles.length;i++){
                    navtpEles[i].style.cssText = 'background: rgba(52,152,213,.9);';
                }
                desktop.frameZindex++;
				console.log(desktop.menuCursor);
            }catch (error){
                console.error(error)
            }
		});

        //退出登出页面事件
        eventBind('click','.xdlogout',function(e){
            try{
                findElements('.xdlogout')[0].style.display = 'none';
            }catch (error){
                console.error(error)
            }
        });

        eventBind('click','.closeAllnav',function(e){
            try{
                var navtp = findElements('.navtp')
					,frames = findElements('.xdframe');
                if(navtp.length>0){
                    for(var i=0;i<navtp.length;i++){
                    	if(navtp[i].remove){
                            navtp[i].remove();
                            frames[i].remove();
						}else{//IE兼容问题
                            navtp[i].parentElement.removeChild(navtp[i]);
                            frames[i].parentElement.removeChild(frames[i]);
						}

                    }
				}else{
                    layer.msg('没有任务哦', function(){});
				}
            }catch (error){
                console.error(error)
            }
        });
		//分屏事件
		eventBind('click','.splitScreen',function (e) {
            layer.msg('暂不支持', function(){});
            return;
			try{
				var splitScreen = findElements('.splitScreen')[0]
					,frameContentEles = findElements('.xdframe')
                    ,frameEles = findElements('.xdframe iframe')
					,text,state;
				if(splitScreen.getAttribute('datastate')=='0'){
					text = '关闭分屏';
                    state = '1';
				}else{
                    text = '开启分屏';
                    state = '0';
					for(var i=0;i<frameContentEles.length;i++){
                        frameContentEles[i].style.cssText = 'z-index:'+frameContentEles[i].style.zIndex+';left:0px;width:100%;';
                       // frameEles[i].style.cssText = 'width:100%;height:100%;';
                    }
				}
                splitScreen.setAttribute('datastate',state)
                splitScreen.innerText = text;
			}catch (error){
				console.error(error);
			}
        })

        //绑定左侧箭头事件
        eventBind('click','.menuleftgif',function(e){
            try{
                var menuli = findElements('.xdmenusdiv>ul>li');

                for(var i=0;i<menuli.length;i++){
                    if(i>=desktop.menuCursor-(6*2)&&i<desktop.menuCursor-6){
                        menuli[i].style.cssText = 'display:block;';
                    }else{
                        menuli[i].style.cssText = 'display:none;';
                    }
                }

                desktop.menuCursor = desktop.menuCursor-6;
                if(6==desktop.menuCursor){
                    findElements('.menuleftgif')[0].style.cssText = 'visibility:hidden;opacity: 0;';
                    findElements('.menurightgif')[0].style.cssText = 'visibility:visible;opacity: 1;';
                }

                if(6<desktop.menuCursor&&desktop.menuCursor<=(parseInt(findElements('.xdmenusdiv>ul>li').length/6)*6)){
                    findElements('.menurightgif')[0].style.cssText = 'visibility:visible;opacity: 1;';
                }

            }catch(e){
                console.error(e);
            }
        });

		//绑定右侧箭头事件
		eventBind('click','.menurightgif',function(e){
			try{
				var menuli = findElements('.xdmenusdiv>ul>li');
				for(var i=0;i<menuli.length;i++){
					if(i>=desktop.menuCursor&&i<desktop.menuCursor*2){
						menuli[i].style.cssText = 'display:block';
					}else{
						menuli[i].style.cssText = 'display:none';
					}
				}
				
				if(parseInt(menuli.length/6)*6==desktop.menuCursor){
					findElements('.menurightgif')[0].style.cssText = 'visibility:hidden;opacity: 0;';
				}else{
					if(parseInt(menuli.length/6)*6>6){
						findElements('.menuleftgif')[0].style.cssText = 'visibility:visible;opacity: 1;';	
					}
				}
				
				desktop.menuCursor = desktop.menuCursor+6;
			}catch(e){
				console.error(e);
			}
		});
		
	}
	
	//元素查找函数
	function findElements(select){
		return document.querySelectorAll(select);
	}
	
	//元素事件绑定函数
	function eventBind(action,select,callback){
		try{
			var eles = findElements(select);
			if(eles&&eles.length>0){
				for(var i=0;i<eles.length;i++){
					eles[i].addEventListener(action,callback,false);	
				}
			}
		}catch(error){
			console.error(error);
		}
	}
	
	//清除元素绑定事件
	function removeBind(action,select,callback){
		try{
			var eles = findElements(select);
			if(eles&&eles.length>0){
				for(var i=0;i<eles.length;i++){
					eles[i].removeEventListener(action,callback,false);	
				}
			}
		}catch(error){
			console.error(error);
		}
	}
		
	//封装ajax请求
	function ajax(){ 
	  var ajaxData = { 
	    type:arguments[0].type || "GET", 
	    url:arguments[0].url || "", 
	    async:arguments[0].async || "true", 
	    data:arguments[0].data || null, 
	    dataType:arguments[0].dataType || "text", 
	    contentType:arguments[0].contentType || "application/x-www-form-urlencoded", 
	    beforeSend:arguments[0].beforeSend || function(){}, 
	    success:arguments[0].success || function(){}, 
	    error:arguments[0].error || function(){} 
	  } 
	  ajaxData.beforeSend() 
	  var xhr = null;
	  if (window.ActiveXObject) {
	    xhr = new ActiveXObject("Microsoft.XMLHTTP");  
	  } else if (window.XMLHttpRequest) {  
	    xhr = new XMLHttpRequest();
	  }

	  xhr.open(ajaxData.type,ajaxData.url,ajaxData.async);
        xhr.responseType=ajaxData.dataType;
        xhr.setRequestHeader("Content-Type",ajaxData.contentType); 
	  xhr.send(function(ajaxData){
		if( typeof ajaxData.data === 'object' ){ 
		    var convertResult = "" ;  
		    for(var c = 0; c < ajaxData.data.length; c++){ 
		      convertResult+= c + "=" + ajaxData.data[c] + "&";  
		    }  
		    convertResult=convertResult.substring(0,convertResult.length-1) 
		    return convertResult; 
		  }else{ 
		    return ajaxData.data; 
		  } 
	  }); 
	  
	  xhr.onreadystatechange = function() {  
	    if (xhr.readyState == 4) {  
	      if(xhr.status == 200){ 
	        ajaxData.success(xhr.response) 
	      }else{ 
	        ajaxData.error() 
	      }  
	    } 
	  }  
	}

	//根据屏幕变化动态修改内容区域高度
	function changeContent(){
		try{
			var height = document.body.clientHeight;
			var eleContents = findElements('.xdcontent');
			eleContents[0].style.cssText = "height:"+(height-131)+"px;";
		}catch(error){
			console.error(error);
		}
	}
	
	//监听全局窗口大小变化
	window.onresize=function(){
 		changeContent();    
	}
	
	init();//初始化
}());
