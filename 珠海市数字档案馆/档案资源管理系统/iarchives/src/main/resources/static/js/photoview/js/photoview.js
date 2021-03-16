;(function(undefined) {
    var _global;
    function PhotoView(attr){
        var that = this;
        this.attr = attr;
        this._pw_img_view;//图片显示区域
        this._pw_img;//图片
        this._pw_msg;//长宽信息
        this._pw_msg_title;    //图片名
        this.initWidth = '30%';//图片初始比例
        this.current = 0;//旋转标识
        this.timer = 0;//信息刷新定时器
        this._init = function(){//初始化
            if(!attr||!this.getEle('#'+attr.eleid)){
                console.error('不存在此元素');
                return;
            }
            var outer = attr.outer?attr.outer:{};
            var isCommon = attr.isCommon?attr.isCommon:false;
            var outer_display = isCommon?'show':'hide';
            var pw_tips_display = attr.src?'hide':'show';
            var pw_img_display = pw_tips_display!='show'?'show':'hide';
            var contentHtml = '<div class="pw-content-view"><div class="pw-msg-title" ></div><div class="pw-msg"></div><div class="pw-img-view" style="height: 100%;width: 100%"><img id="photo_img" src="'+(attr.src?attr.src:'')+'" class="pw-img  '+pw_img_display+'"/>'+
                '<div class="pw-tips '+pw_tips_display+'">无图片显示</div></div></div><div class="pw-btn-view"><div class="pw-btn-flex">'+
                '<div class="pw-btn '+outer_display+'" outer="true" eval="previous"><img src="../js/photoview/img/previous.png"/>上一条</div>'+
                '<div class="pw-btn '+outer_display+'" outer="true" eval="next"><img src="../js/photoview/img/next.png"/>下一条</div>'+
                '<span class="pw-btn-split  '+outer_display+'">|</span> '+
                '<div class="pw-btn" eval="magnify"><img src="../js/photoview/img/magnify.png"/>放大</div>'+
                '<div class="pw-btn" eval="narrow"><img src="../js/photoview/img/narrow.png"/>缩小</div>'+
                '<div class="pw-btn" eval="turnLeft"><img src="../js/photoview/img/turnLeft.png"/>左转</div>'+
                '<div class="pw-btn" eval="turnRight"><img src="../js/photoview/img/turnRight.png"/>右转</div>'+
                '<div class="pw-btn" eval="restore"><img src="../js/photoview/img/restore.png"/>还原</div>'+
                '<div class="pw-btn '+outer_display+'" outer="true" eval="senior" ><img src="../js/photoview/img/senior.png"/>高级</div>'+
                '<span class="pw-btn-split  '+outer_display+'">|</span> '+
                '<div class="pw-btn '+outer_display+'" outer="true" eval="exitda"><img src="../js/photoview/img/exitda.png"/><span>退档</span></div>'+
                '<div class="pw-btn '+outer_display+'" outer="true" eval="pass"><img src="../js/photoview/img/pass.png"/>通过</div>'+
                '<div class="pw-btn '+outer_display+'" outer="true" eval="back"><img src="../js/photoview/img/back.png"/>返回</div>'+
                '</div></div>';
            that.getEle('#'+attr.eleid).innerHTML =  contentHtml;
            that._pw_img = this.getEle('#'+attr.eleid+' .pw-img'); //获取显示区域图片
            that._pw_msg_title = this.getEle('.pw-msg-title');
            var img_view = this.getEle('#'+attr.eleid+' .pw-img-view');//获取图片面板
            that._pw_img.onmousedown = function(e){//清除图片默认行为
                e.preventDefault();
            };
            that._pw_img_view =  img_view;
            that._pw_msg = this.getEle('#'+attr.eleid+' .pw-msg');
            if(!attr.initWidth){
                setTimeout(function(){//自适应屏幕
                    if(that._pw_img.offsetWidth>that._pw_img.offsetHeight){
                        that.initWidth = (that._pw_img_view.offsetWidth-20)+'px';
                    }else{
                        var w_h = that._pw_img.offsetWidth/that._pw_img.offsetHeight;
                        that.initWidth = that._pw_img_view.offsetHeight*0.93*w_h+'px';
                    }
                    that.restore();
                },200);
            }else{
                that.initWidth = attr.initWidth;
                that.restore();
            }
            var _pw_btns = document.querySelectorAll('#'+attr.eleid+' .pw-btn');
            // var _pw_view_height = this.getEle('#'+attr.eleid).offsetHeight;//获取框架高度
            // var _pw_btn_height = this.getEle('.pw-btn-view').offsetHeight;//获取按钮栏高度
            // that.getEle('.pw-content-view').style.height = _pw_view_height+'px';//设置图片中心区域高度（修改:去除按钮区域高度,图片全屏显示）
            var x,y,l,t = 0;
            var isDown = false;
            that._pw_img_view.onmousedown=function(e){//鼠标按下
                x = e.clientX;//获取x坐标和y坐标
                y = e.clientY;
                l = that._pw_img_view.offsetLeft;//获取左部和顶部的偏移量
                t = that._pw_img_view.offsetTop;
                isDown = true;
            }
            document.onmouseup = function()//鼠标抬起
            {
                isDown = false;
            }

            document.onmousemove = function(e){//鼠标移动
                if (isDown != false) {
                    var nx = e.clientX;//获取x和y
                    var ny = e.clientY;
                    var nl = nx - (x - l);//计算移动后的左偏移量和顶部的偏移量
                    var nt = ny - (y - t);
                    that._pw_img_view.style.left = nl + 'px';
                    that._pw_img_view.style.top = nt + 'px';
                    that.changeMsg();
                }
            }

            var scrollFunc = function (e) {//滚轮处理(放大缩小)
                e = e || window.event;
                var t = 0;
                t=(e.wheelDelta)?e.wheelDelta/120:-(e.detail||0)/3;//兼容性处理
                if (t > 0) {
                    that.magnify();
                } else if (t < 0) {
                    that.narrow();
                }
            }

            document.onkeydown=function(event){
                var e = event || window.event || arguments.callee.caller.arguments[0];
                if(e && e.keyCode==37){ //左
                    eval('that.previous()');
                }
                if(e && e.keyCode==39){ //右
                    eval('that.next()');
                }
            };

            img_view.addEventListener('DOMMouseScroll', scrollFunc, false);
            img_view.onmousewheel = scrollFunc;
            for(var i in _pw_btns){//按钮事件绑定
                if(!isNaN(i)){//排除length属性
                    var isOuter = _pw_btns[i].getAttribute('outer');
                    var method = _pw_btns[i].getAttribute('eval');
                    if(isOuter&&outer[method]){
                        that['_'+method] = outer[method]['attr'];
                        that[method] = outer[method]['method'];
                    }
                    that.eventBind('click',_pw_btns[i],eval('that.'+method));
                }
            }
        }

        this.magnify = function(){//放大
            that._pw_img.style.width = that._pw_img.offsetWidth*1.1+'px';
            that.changeMsg();
        }

        this.narrow = function(){//缩小
            that._pw_img.style.width = that._pw_img.offsetWidth*0.9+'px';
            that.changeMsg();
        }

        this.turnLeft = function(){//左转
            that.current = (that.current-90);
            that._pw_img_view.style.transform = 'rotate('+that.current+'deg)';
        }

        this.turnRight = function(){//右转
            that.current = (that.current+90);
            that._pw_img_view.style.transform = 'rotate('+that.current+'deg)';
        }

        this.restore = function(){//还原
            that.current = 0;
            that._pw_img.style.width = that.initWidth;
            that._pw_img_view.style.top = '0px';
            that._pw_img_view.style.left = '0px';
            that._pw_img_view.style.transform = 'rotate(0deg)';
            that.changeMsg();
        }

        this.changeImg = function(src,title){//改变显示图片
            var tips = that.getEle('#'+that.attr.eleid+' .pw-tips');
            var tipMsg = that.getEle('#'+that.attr.eleid+' .pw-msg-title');
            that.attr.src = src;
            that.current = 0;
            tips.innerText = '无图片显示';
            tipMsg.classList.remove('show');
            tipMsg.classList.add('hide');
            that._pw_img.classList.remove('show');
            that._pw_img.classList.add('hide');
            that._pw_msg.innerText = '宽:'+0+'px;高:'+0+'px';
            if(src){
                tips.innerText = '图片加载中...';
                var img = new Image();
                img.onload = function(){
                    that._pw_img.setAttribute('src',src);
                    that._pw_msg_title.innerText = title?title:'';
                    tips.classList.remove('show');
                    tips.classList.add('hide');
                    tipMsg.classList.remove('hide');
                    tipMsg.classList.add('show');
                    that._pw_img.classList.remove('hide');
                    that._pw_img.classList.add('show');
                    setTimeout(function(){
                        if(that._pw_img.offsetWidth>that._pw_img.offsetHeight){
                            that.initWidth = (that._pw_img_view.offsetWidth-20);
                        }else{
                            var w_h = that._pw_img.offsetWidth/that._pw_img.offsetHeight;
                            that.initWidth = that._pw_img_view.offsetHeight*0.93*w_h;
                        }
                        if(that.initWidth<20){//解决图片宽度过小导致过度缩小不显示问题
                            that.initWidth = 300;
                        }
                        that.initWidth += 'px';
                        that.restore();
                    },200)
                }
                img.onerror = function(){
                    tips.classList.remove('hide');
                    tips.classList.add('show');
                    tips.innerText = '图片不存在或加载失败';
                }
                img.src = src;//放在事件后面,避免IE特异性错误
            }
        }


        this.changeMsg = function(){//动态修改大小显示信息
            that.changeMsgTemp();
            clearTimeout(that.timer);
            that.timer = setTimeout(function(){//解决设置宽度后高度渲染滞后问题
                that.changeMsgTemp();
            },300);
        }

        this.changeMsgTemp = function(){
            var w = that._pw_img.offsetWidth;
            var h = that._pw_img.offsetHeight;
            that._pw_msg.innerText = '宽:'+w+'px;高:'+h+'px';
        }

        this.getImgMsg = function(){//获取长宽以及角度
            return {
                width:that._pw_img.offsetWidth,
                height:that._pw_img.offsetHeight,
                deg:that.current%360
            }
        }

        this._init();
    }


    PhotoView.prototype.getEle = function(selector){//根据选择器获取元素
        return document.querySelector(selector);
    }

    PhotoView.prototype.eventBind = function(action,ele,callback){//事件绑定
        try{
            ele.addEventListener(action,callback,false);
        }catch(error){
            console.error(error);
        }
    }

    _global = (function(){ return this || (0, eval)('this'); }());
    if (typeof module !== "undefined" && module.exports) {
        module.exports = PhotoView;
    } else if (typeof define === "function" && define.amd) {
        define(function(){return PhotoView;});
    } else {
        !('PhotoView' in _global) && (_global.PhotoView = PhotoView);
    }
}());
