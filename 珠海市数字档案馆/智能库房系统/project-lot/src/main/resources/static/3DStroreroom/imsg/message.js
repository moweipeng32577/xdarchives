(function () { //匿名函数
    var option = {
        title: "消息通知", //标题
        width: 250,
        msgData: [], //消息数据
        noticeData: [], //提醒数据
        msgUnReadData: 0, //消息未读数
        noticeUnReadData: 0, //提醒未读数
        elem: "body",
        msgClick:null, //消息点击回调事件
        noticeClick: null, //提醒点击回调事件
        msgShow: 5, //消息展示条数
        noticeShow: 5, //提醒展示条数
        height: 350,
        server:"",
        getNodeHtml: function(obj, node) { //消息提醒自定义显示html
                if (obj.readStatus == 1) {
                    node.isRead = true;
                } else {
                    node.isRead = false;
                }
                var html = "<p>"+ obj.text +"</p>";
                node.html = html;
                return node;
        }
    };

    var megApi = {
        nodeList: [],
        tp: null,

        config: function(ops) {
            $.extend(true, option, ops);
            this.nodeList = [];
            return this;
        },

        /**
         * 初始化
         * @param ops
         * @returns {api}
         */
        init: function(ops) {
            ops !== undefined && this.config(ops);

            if (option.elem === undefined || typeof option.elem !== "string") {
                throw "option.elem is undefined";
            }

            var clazz = $(option.elem).attr("class");
            $(option.elem).attr("class", clazz?clazz:"" + " message-bell");

            this.bellDraw();
            this.listener();
            this.noticeData();

            return this;
        },

        /**
         * 审批完毕推送消息
         * */
        sendData:function (message) {
            layui.use('layer',function () {
                layer.msg(message);
            });
            option.noticeData=[];
            this.init();
        },

        /**
         * 初始化列表数据
         * */
        noticeData:function () {
            var _this = this;
            var server = option.server;

            //查找没被查阅过的借档信息
            $.ajax({
                type:'GET',
                url: server+'/deviceDetail/getMessage',
                async:true,
                success:function(res, opt){
                    for(var i = 0; i<res.data.length; i++){
                        var data = res.data[i];
                        var message = {};
                        message.id = data[0];
                        message.text = data[1]+'-'+data[2];
                        message.readStatus = "0";
                        option.noticeData.push(message)
                    }
                    option.noticeUnReadData=res.data.length;
                    _this.bellDraw();
                }
            });
        },

        /**
         * 打开显示档案位置信息
         * */
        showGrid:function (docid) {
            var _this = this;
            var server = option.server;
            layui.use(['element','layer','table'],function(){
                var table = layui.table;
            });
            layer.open({
                type: 2,
                title:'档案实体位置',
                area: ['90%', '70%'],
                content:'../html/messageFromTable.html',
                btn:['确定'],
                yes:function (index) {
                    $.ajax({
                        type:'GET',
                        url: server+'/deviceDetail/setMessageStatus?docid='+ docid,
                        async:true,
                        success:function(res, opt){
                            layer.close(index); //关闭弹窗
                            option.noticeData=[];
                            _this.init();
                        }
                    });
                },
                success: function(layero, index){
                    var iframe = window['layui-layer-iframe'+index];
                    //调用子页面的全局函数
                    iframe.child(server,docid)
                },
            });
            $(".message-frame").remove();
        },

        /**
         * 消息按钮渲染
         * @param timestamp
         * @returns {*}
         */
        bellDraw: function () {
            var bellHtml =
                // "<span data-type='1' class='message-bell-btn' "+ (option.msgUnReadData > 0?("title='"+ option.msgUnReadData + "条新消息'"):"") +">" +
                // "<i class='fa fa-bell-o'></i>"+ (option.msgUnReadData > 0?"<span class='badge-dot'></span>":"") +"</span>" +
                "<span data-type='2' class='message-bell-btn' "+ (option.noticeUnReadData > 0?("title='"+ option.noticeUnReadData +"条新提醒'"):"") +">" +
                "<img  class='message-img' src='./img/message_cover.png'/>" + (option.noticeUnReadData > 0?("<span class='badge'>"+ option.noticeUnReadData +"</span>"):"") +
                "</span>";
            $(option.elem).html(bellHtml);
        },


        /**
         * 事件监听
         * @param timestamp
         * @returns {*}
         */
        listener: function() {
            var that = this;

            /**
             * 铃铛按钮点击
             */
            $(option.elem).on("click", ".message-bell-btn", function() {
                if ($(".message-frame").length > 0) {
                    $(".message-frame").remove();
                }
                var type = $(this).attr("data-type");
                that.tp = type;
                that.contentListDraw(type);
                that.caculatePosition(this);
            });

            /**
            * 消息点击
            */
            $(document).on("click", ".message-content-list li", function() {
                if ($(this).find(".badge-dot").length > 0) {
                    var type = that.tp;
                    //按钮1（不使用）
                    if (type == 1) {
                        // (option.msgClick&&typeof option.msgClick == "function")?option.msgClick(this):""; //回调函数
                    }
                    //按钮2
                    else if (type == 2) {
                        // (option.noticeClick&&typeof option.noticeClick == "function")?option.noticeClick(this):"";
                        var data  =$(this).context.childNodes[2].textContent;
                        that.showGrid(data);
                    }

                    if ($(this).find(".badge-dot").length > 0) {
                        if (type == 1) {
                            option.msgUnReadData>0?option.msgUnReadData -= 1:"";
                        }
                        else if (type == 2) {
                            option.noticeUnReadData>0?option.noticeUnReadData -= 1:"";
                        }
                        $(this).find(".badge-dot").remove();
                    }
                }

            });

            /**
             * 加载更多
             */
            $(document).on("click", ".message-footer", function() {
                var ct = $(".message-content-list li").length;
                var dataType = that.tp;
                var arr = dataType==1?option.msgData:option.noticeData;
                var count = 0;
                for (var i=ct; i<arr.length; i++) {
                    if (arr[i]) {
                        var node = that.getNode(arr[i], dataType);
                        that.nodeList.push(node);
                        count ++;
                    }

                    if (count > option.msgShow) {
                        break;
                    }
                }

                var html = that.getNodelistHtml();

                $(".message-content-list").html(html);

                if (arr.length <= that.nodeList.length) {
                    $(this).hide();
                }
            });

            /**
             * 点击其他地方隐藏消息框
             */
            $(document).click(function(e){
                var src = e.target;
                var arr = $(".message-frame *");
                var flag = true;
                if(src.className && src.className.match("message-bell-btn")
                    || src.parentElement && src.parentElement.className.match("message-bell-btn")){
                    flag = false;
                }else{
                    if (arr && arr.length > 0) {
                        for (var i = 0; i < arr.length; i++) {
                            if (src && src === arr[i]) {
                                flag = false;
                            }
                        }
                    }
                    if (src.className && src.className.match("message-frame")) {
                        flag = false;
                    }
                }
                if (flag) {
                    $(".message-frame").remove();
                    that.nodeList = [];
                    that.tp = null;
                }
            });
        },

        /**
         * 消息列表渲染
         * @param type
         */
        contentListDraw: function(type) {
            var fr = "";
            if ($(".message-frame").length == 0) {
                fr += "<div class='message-frame animated fadeIn' style='width: "+ (option.width?option.width:"350") +"px' ></div>";
                $(option.elem).after(fr);
            }

            this.nodeList = [];

            var div = "<div class='message-frame-header'><span style='height: 40px;line-height: 40px;text-align: center;width: 50px;    display: inline-block;'>"+ (type==1?"消息":"提醒") +"</span> " +
                // "<button class='message-btn message-btn-blue message-btn-header' data-type='"+ type +"'>全部已读</button>" +
                "</div>"+
                "<div class='message-content' style='max-height:"+ (option.height?option.height+"px":"350px") +"'>";

            //按钮1（暂不使用）
            if (type == 1) {
                if (option.msgData && option.msgData.length > 0) {
                    div += "<ul class='message-content-list'>";
                    for (var i=0; i < (option.msgData.length<option.msgShow?option.msgData.length:option.msgShow); i++) {
                        var node = this.getNode(option.msgData[i], type);
                        this.nodeList.push(node);
                    }
                    var html = this.getNodelistHtml();
                    div += html;
                    div += "</ul>";
                }
                else {
                    div += "<div class='message-none-msg'>无消息</div>";
                }
                div += "</div>";

                if (this.nodeList.length < option.msgData.length) {
                    div += "<div class='message-footer' data-type='"+ type +"'><span>加载更多</span></div>";
                }
            }

            // 按钮2
            else if (type == 2) {
                if (option.noticeData && option.noticeData.length > 0) {
                    div += "<ul class='message-content-list'>";
                    for (var i=0; i < (option.noticeData.length<option.noticeShow?option.noticeData.length:option.noticeShow); i++) {
                        var node = this.getNode(option.noticeData[i], type);
                        this.nodeList.push(node);
                    }
                    var html = this.getNodelistHtml();
                    div += html;
                    div += "</ul>";
                } else {
                    div += "<div class='message-none-msg'>无提醒</div>";
                }
                div += "</div>";

                if (this.nodeList.length < option.noticeData.length) {
                    div += "<div class='message-footer' data-type='"+ type +"'><span>加载更多</span></div>";
                }
            }

            $(".message-frame").html(div);
        },

        /**
         * 计算位置
         * @param elemt
         */
        caculatePosition: function(elemt) {
            //计算位置
            var screenX = window.innerWidth; //屏幕X
            var screenY = window.innerHeight; //屏幕Y

            var objRect = elemt.getBoundingClientRect();

            var objX = objRect.left; //按钮的左边距原点的距离
            var objY = objRect.bottom; //按钮底距顶部的距离

            var editPx = 10;

            var divWidth = $(".message-frame").outerWidth();
            var divHeight = $(".message-frame").outerHeight();

            while (objX + divWidth > screenX) {
                objX -= editPx;
            }
            var divX = objX;

            while (objY + divHeight > screenY) {
                objY -= editPx;
            }

            var divY = objY;

            $(".message-frame").css('position', "fixed").css('left', divX).css('top', divY);
        },

        /**
         * 每条消息组装
         * @param obj
         * @param type
         * @returns {{type: *}}
         */
        getNode: function(obj, type) {
            var node = {
                type: type,
                isRead: true,
                id:obj.id
            };
            var nd = (option.getNodeHtml&&typeof option.getNodeHtml == "function")?option.getNodeHtml(obj, node):null;

            nd?$.extend(true, node, nd):node;

            var html = "<li>";
                if (!node.isRead) {
                    html += "<div class='message-content-list-item-detail message-content-list-item-dot'><span class='badge-dot'></span></div>"
                }
                html += "<div class='message-content-list-item-content'>"+ node.html + "</div>";
                html += "<span style='display: none' id='data'>"+node.id + "</span>";
                html += "</li>";
            node.html = html;
            return node;
        },

        /**
         * 组装消息列表的html
         * @returns {string}
         */
        getNodelistHtml: function() {
            var list = this.nodeList;
            var html = "";
            for (var i=0; i<list.length; i++) {
                html += list[i].html;
            }

            return html;
        }
    };

    window.MessagePlugin = megApi;
})();