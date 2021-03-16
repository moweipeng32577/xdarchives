/*
 Author：温金华
 Date：2017.08.07
 Version：list 1.0
 */
$(function(){
	$.fn.extend({
		ilist:function(options){
			
			//初始化参数
			var option = $.extend({
				click:function(a){ }
			},options);
			
			
			option.list=this;	
			
			/*初始化方法*/
			option._init=function(){
				var html = option._createtree(option.data);
				//option.list.append(html);
				//console.log(html);
			}
			
			/*节点构造方法*/
			option._createtree = function(data){
				var json = data;
				var childs = [];
				var html = '<ul>';

				for(var i=0;i<json.length;i++){
					if(json[i].isp!=1&&json[i].isp!='daxt'){
						childs.push(json[i]);
						continue;		
					}
                    var evenclass = '';
					if(i%2!=0){
                        evenclass = 'listlibg';
					}
					html += ' <li class="'+evenclass+'" key="'+json[i].tkey+'" code="'+json[i].code+'" isp="1" url="'+json[i].url+'"><a ><div class="leftd"><img style="margin-left:15px;" class="desktopicon" src="img/icon/'+json[i].icon+'">  &nbsp;&nbsp;&nbsp;'+json[i].name+'</div><div class="rightd"> ';
					if(json[i].haschilds=='true'){
						html += '<i class="iconRight icon icon-chevron-right"></i></div></a><div class="subulli"><ul></ul></div>';
					}else{
						//html += '<i class="iconRight icon icon-location-arrow"></i></div></a><div class="subulli"><ul></ul></div>';
                        html += '</div></a><div class="subulli"><ul></ul></div>';
					}
					
				}

				html += '</li></ul>';

				option.list.append(html);
				//console.log(childs);
				
				for(var i=0;i<childs.length;i++){
                    var evenclass = '';
                    if(i%2!=0){
                        evenclass = 'listlibg';
                    }
					html = '<li class="'+evenclass+'" key="'+childs[i].tkey+'" code="'+childs[i].code+'" isp="0" url="'+childs[i].url+'"><a class="sech"><img class="desktopicon" src="img/icon/'+childs[i].icon+'">  '+childs[i].name+'</a></li>';
					option.list.find('ul li[code="'+childs[i].code+'"] ul').append(html);  
				}

				var listUls = option.list.find('ul li[isp="1"]');
                for(var i=0;i<listUls.length;i++){
					if($(listUls[i]).find('ul li').length==0){
                        $(listUls[i]).find('.iconRight').removeClass('icon-chevron-right');//.addClass('icon-location-arrow');
					}
				}
				//return html;
			}
			
			
			/* 动态菜单项接受单击 */
			this.on('click','li',function(event){
				 event.stopPropagation();
				//alert($(this).find('i').hasClass('icon-tag'));
				if($(this).find('ul li').length>0){
					return false;
				}
				
				option.click($(this));
				
			});
			
			
			option._init();
			
		}
		
	});
});