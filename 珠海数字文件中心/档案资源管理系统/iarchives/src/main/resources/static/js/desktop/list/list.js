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
				option.list.append(html);
				//console.log(html);
			}
			
			/*节点构造方法*/
			option._createtree = function(data){
				var json = data;
				var html = '<ul>';
				for(var i=0;i<json.length;i++){
					html += '<li key="'+json[i].key+'" code="'+json[i].code+'" isp="1" url="'+json[i].url+'"><a ><div class="leftd"><i class="icon '+json[i].icon+'"></i> '+json[i].name+'</div><div class="rightd"> ';
					if(json[i].childs&&json[i].childs.length>0){
						html += '<i class="icon icon-chevron-right"></i></div></a>';
					}else{
						html += '<i class="icon icon-tag"></i></div></a>';
					}
					
					html += '<ul>';
					for(var j=0;j<json[i].childs.length;j++){
						html += '<li key="'+json[i].childs[j].key+'" code="'+json[i].code+'" isp="0" url="'+json[i].childs[j].url+'"><a class="sech"><i class="icon '+json[i].childs[j].icon+'"></i> '+json[i].childs[j].name+'</a></li>';
					}
					html += '</ul></li>';
					
				}
				
				html += '</ul>';
				return html;
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