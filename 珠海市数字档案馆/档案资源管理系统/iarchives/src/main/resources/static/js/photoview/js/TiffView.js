;(function(undefined) {
    var _global;
    function TiffView(attr){
        var that=this;
        this.attr=attr;
        this.initWidth='30%';
        this.current=0;
        this.now=0;
        this.img_lenth = null;
        this.tif = null;
        this.filesrc;

        this.xhr=new XMLHttpRequest();
        if (!attr || !this.getEle('#' + this.attr.eleid)) {
            console.error('不存在此元素');
            return;
        }
        var outter = attr.outter ? attr.outter : {};
        this.filesrc = attr.filesrc ? attr.filesrc : {};
        var contentHtml = '<div style="overflow: auto">' +
                                '<div id="pw-img-view" class="pw-img-view" ' +
            'style="width: 100%; height: 100%"> </div>' +
                                    '<div style="float: left"> ' +
                                        '<div id="previous" class="pw-btn" outer="true" eval="previous">' +
                                            '<img src="../js/photoview/img/previous.png" />上一条</div>' +
                                                '<div id="next" class="pw-btn" outer="true" eval="next"><img src="../js/photoview/img/next.png"/>下一条</div></div></div>';
        this.getEle('#' + this.attr.eleid).innerHTML = contentHtml;

        this.xhr.responseType = "arraybuffer";
        this.xhr.open('Get', filesrc);
        this.xhr.send();
        this.xhr.onreadystatechange  = function (ev) {
            var maxWidth=document.getElementById('pw-img-view').parentNode.scrollWidth;
            var maxHeight=document.getElementById('pw-img-view').parentNode.scrollHeight;
            that.tif = new Tiff({buffer: that.xhr.response});
            that.img_lenth = that.tif.countDirectory();
            that.tif.setDirectory(this.now);
            var canvas=that.tif.toCanvas();
           canvas.style.position="absolute";
            canvas.style.margin="auto";
            canvas.style.top="80px";
            canvas.style.left="20px";
            canvas.style.right="20px";
            canvas.style.bottmom="0";
            canvas.style.transition="transform .2s ease";
            canvas.id="canvas";
            canvas.style.height=maxHeight;
            canvas.style.width=maxWidth;
            var p1=that.getEle('#'+"pw-img-view");
            p1.append(canvas);
            that.setInit();
        }


        this.setInit=function () {
            // that._pv_canv= document.getElementById("canvas");
            // that._pv_canv.style.width=this._pv_canv.offsetWidth*0.9+"px";
            // that._pv_canv.style.height=this._pv_canv.offsetHeight*0.9+"px";

        }
        var scrollFunc=function (e) {
            e = e || window.event;
            var t = 0;
            t=(e.wheelDelta)?e.wheelDelta/120:-(e.detail||0)/3;//兼容性处理
            if (t > 0) {
                that.magnify();
            } else if (t < 0) {
                that.narrow();
            }
        }

        this.getEle('#'+"pw-img-view").addEventListener('DOMMouseScroll', scrollFunc, false);
        this.getEle('#'+"pw-img-view").onmousewheel = scrollFunc;

        this.magnify = function(){//放大

            var canvas=document.getElementById("canvas");
            // document.getElementById("canvas").height=document.getElementById("canvas").offsetHeight*1.1;
            // document.getElementById("canvas").width=document.getElementById("canvas").offsetWidth*1.1;
            // var content=canvas.getContext('2d');
            // var width=canvas.getAttribute("width");
            // var height=canvas.getAttribute("height");
            // var data=content.getImageData(0,0,width,height);
            //
            // width=parseInt(width)*1.1;
            // height=parseInt(height)*1.1;
            // canvas.setAttribute("width",width);
            // canvas.setAttribute("height",height);
            // content.putImageData(data,0,0);
            // var context=canvas.getContext('2d');
            // var width=canvas.getAttribute("width");
            // var height=canvas.getAttribute("height");
            // var data=context.getImageData(0,0,width,height);
            // canvas.setAttribute("width",parseInt(width)*1.1);
            // canvas.setAttribute("height",parseInt(height)*1.1);
            //
            // context.putImageData(data,0,0,0,0,width*1.1,height*1.1);
            // context.drawImage(canvas,0,0,width,height,0,0,width*1.1,height*1.1);
            var maxWidth=document.getElementById('pw-img-view').parentNode.scrollWidth;
            var maxHeight=document.getElementById('pw-img-view').parentNode.scrollHeight;
            var canvas=document.getElementById("canvas");
            var context=canvas.getContext('2d');
            var width=canvas.getAttribute("width");
            var height=canvas.getAttribute("height");
            var data=context.getImageData(0,0,width,height);
            var width1=that.tif.toCanvas().width;
            var height1=that.tif.toCanvas().height;
            if(width<maxWidth&&height<maxHeight){
            canvas.setAttribute("height",parseInt(height)*1.1);
            canvas.setAttribute("width",parseInt(width)*1.1);
            // context.putImageData(data,0,0,0,0,width*0.9,height*0.9);
            context.drawImage(that.tif.toCanvas(),0,0,width1,height1,0,0,width*1.1,height*1.1);}
        }

        this.narrow = function(){//缩小
            var canvas=document.getElementById("canvas");
            var context=canvas.getContext('2d');
            var width=canvas.getAttribute("width");
            var height=canvas.getAttribute("height");
            var data=context.getImageData(0,0,width,height);
            var width1=that.tif.toCanvas().width;
            var height1=that.tif.toCanvas().height;
            canvas.setAttribute("height",parseInt(height)*0.9);
            canvas.setAttribute("width",parseInt(width)*0.9);
           // context.putImageData(data,0,0,0,0,width*0.9,height*0.9);
            context.drawImage(that.tif.toCanvas(),0,0,width1,height1,0,0,width*0.9,height*0.9);
        }

        var x,y,l,t = 0;
        var isDown = false;
        document.getElementById('pw-img-view').onmousedown=function (e) {
            x=e.clientX;
            y=e.clientY;
            l=document.getElementById('pw-img-view').offsetLeft;
            t=document.getElementById('pw-img-view').offsetTop;
            isDown=true;
        }
        document.onmouseup=function (e) {
            isDown=false;
        }
        document.onmousemove=function (e) {
            if(isDown==true){
                var nx = e.clientX;//获取x和y
                var ny = e.clientY;
                var nl = nx - (x - l);//计算移动后的左偏移量和顶部的偏移量
                var nt = ny - (y - t);
                document.getElementById('pw-img-view').style.left = nl + 'px';
                document.getElementById('pw-img-view').style.top=nt+"px";
            }
        }
        document.getElementById('next').onclick=function (e) {
            that.now++;
            if(that.now>that.img_lenth){
                that.now=that.img_lenth-1;
            }
            that.tif.setDirectory(that.now);
            var p1=that.getEle('#'+"pw-img-view");
            p1.removeChild(that.getEle("canvas"));
            that._pv_canv=that.tif.toCanvas();
            that._pv_canv.style.position="absolute";
            that._pv_canv.style.margin="auto";
            that._pv_canv.style.top="80px";
            that._pv_canv.style.left="20px";
            that._pv_canv.style.right="0";
            that._pv_canv.style.bottmom="0";
            that._pv_canv.style.transition="transform .2s ease";
            that._pv_canv.id="canvas";
            that._pv_canv.class="pw-img show"
            p1.append(that._pv_canv);
            that.setInit();
        }

        document.getElementById('previous').onclick=function (ev) {
            that.now--;
            if(that.now<0){
                that.now=0;
            }
            that.tif.setDirectory(that.now);
            var p1=that.getEle('#'+"pw-img-view");
            p1.removeChild(that.getEle("canvas"));
            that._pv_canv=that.tif.toCanvas();
            that._pv_canv.style.position="absolute";
            that._pv_canv.style.margin="auto";
            that._pv_canv.style.top="80px";
            that._pv_canv.style.left="20px";
            that._pv_canv.style.right="0";
            that._pv_canv.style.bottmom="0";
            that._pv_canv.style.transition="transform .2s ease";
            that._pv_canv.id="canvas";
            p1.append(that._pv_canv);
            that.setInit();
        }

    }
    TiffView.prototype.getEle = function(selector){//根据选择器获取元素
        return document.querySelector(selector);
    }
    TiffView.prototype.eventBind = function(action,ele,callback){//事件绑定
        try{
            ele.addEventListener(action,callback,false);
        }catch(error){
            console.error(error);
        }
    }

    _global = (function(){ return this || (0, eval)('this'); }());
    if (typeof module !== "undefined" && module.exports) {
        module.exports = TiffView;
    } else if (typeof define === "function" && define.amd) {
        define(function(){return TiffView;});
    } else {
        !('TiffView' in _global) && (_global.TiffView = TiffView);
    }
})();