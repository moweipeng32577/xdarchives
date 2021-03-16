/*
Author：温金华
Date：2017.09.11
Version：imsg 1.0
*/
$(function(){
	$.fn.extend({
		imsg:function(options){
			//初始化参数
			var option = $.extend({
				ftitle:'通知',
				ttitle:'待办事项',
				url:'/user/getmsg',
				clicktr:function(obj){ },
                clicki:function(obj){ }
			},options);
			option.msg=this;
            this.msgcount = 0;

			option._init=function(){
				option.data = [];
                $.ajax({
                    type: "post",
                    url: option.url,
                    dataType:'json',
                    success: function (data) {
						option.data = data;
                    },
                    error:function(){
                        layer.msg('通知加载失败...', {icon: 5});
                        // location.href = '/index';
                    },
                    async:false
                });
                option.msg.msgcount = option.data.length;
				this.msg.append(option._createtree(option.data));
			}

			// option.refresh = function(){
			// 	this.msg.removeAll();
			// }

			option._createtree = function(data){
				var html = '<div id="msgpaneldiv"><div id="msgtz"><div class="ititle"><span>'+option.ftitle+'</span></div><div class="tznr"><table class="tztable"><tbody>';
				var flag = true;
                var stickArr = [];//置顶
                var notStickArr = [];//非置顶项
                for(var dataIndex in data){
                    if(data[dataIndex].borrowmsgid=='*'){
                        stickArr.push(data[dataIndex]);
                        continue;
                    }
                    notStickArr.push(data[dataIndex]);
                }

                data = stickArr.concat(notStickArr);
                for(var i=0;i<data.length;i++){
                    if(data[i].msgtype!=1){
                        continue;
                    }
                    flag = false;
                    var	text = data[i].msgtypetext.length > 10 ? data[i].msgtypetext.substring(0,10) + '...':data[i].msgtypetext;
                    var msgtypetext = data[i].msgtypetext;
                    var isStick = '';
                    if(data[i].borrowmsgid=='*'){
                        isStick = '<span class="stick-cls">置顶</span>';
                    }
                    html += '<tr msgid="'+data[i].msgid+'" msgtitle="'+data[i].msgtypetext+'"><td> &nbsp;公 告 :</td><td>'+text+isStick+'</td><td><i class="icon icon-remove-sign" ></i></td></tr>';
                }
				if(flag){
					html += '<center>无通知...</center>';
				}

				html += '</tbody></table></div></div><div id="msgdbsx"><div' +
					' class="ititle"><span>'+option.ttitle+'</span></div><div class="dbsxnr tznr" style="overflow:' +
					' auto;height: 90%"><table' +
					' class="tztable"><tbody>';
				flag = true;
				for(var i=0;i<data.length;i++){
					if(data[i].msgtype==1){
						continue;
					}
                    var isUrging = '';
					if (undefined != data[i].urging){
                        if(data[i].urging.trim()=="2"){
                            isUrging = '<span class="stick-cls">催</span>';
                        }
                    }
					flag = false;
                    var	text1 = data[i].msgtext.length >18 ? data[i].msgtext.substring(0,18) + '...':data[i].msgtext;
                    var msgtypetext=data[i].msgtypetext;
                    if(msgtypetext.indexOf('查档到期提醒')>-1) {
                        html += '<tr msgid="' + data[i].msgid + '" borrowmsgid="'+data[i].borrowmsgid+'"><td>&nbsp;&nbsp;' + data[i].msgtypetext + ':</td><td>' + text1+isUrging + '</td><td><i class="icon icon-remove-sign" ></i></td></tr>';
                    }else if(msgtypetext.indexOf('实体出库')>-1){
                        html += '<tr msgid="' + data[i].borrowmsgid + '" ><td>&nbsp;&nbsp;' + data[i].msgtypetext + ':</td><td>' + text1 +isUrging+ '</td></tr>';
					}else{
                        html += '<tr msgid="' + data[i].msgid + '" ><td>&nbsp;&nbsp;' + data[i].msgtypetext + ':</td><td>' + text1 +isUrging+ '</td></tr>';
                    }
				}
                if(flag){
                    html += '<center>无待办事项...</center>';
                }
                /*$.ajax({
                    type: "post",
                    url: '/getProductMsg',
                    data:{},
                    success: function (result) {
                        if(result.data[0]=="简化版"||result.data[0]=="单机版"||result.data[0]=="网络版"){//单机版删除信息栏中的通知栏
                            $('#msgtz').remove();
                            document.getElementById("msgpanel").style.height="50%";
                            document.getElementById("msgpaneldiv").style.height="98%";
                            document.getElementById("msgdbsx").style.height="98%";
                        }
                    }
                });*/
				html += '</tbody></table></div></div></div>';
				return html;
			}

            function bindClick(t){
                t.on('click','tr',function(e){
                    console.log(e.target.tagName);
                    if(e.target.tagName=='I'){
                        option.clicki($(this));
                    }else{
                        option.clicktr($(this));
                    }
                });
            }

            bindClick(this);
			
			this.refresh = function(){
				this.unbind('click');
                option.msg.children().remove();
                option._init();
                bindClick(this);
			}
			option._init();

			return this;
		}
		
	});
});
