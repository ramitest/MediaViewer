
    var PICS = 1;
    var VIDS = 2;
    var SELECTED_MEDIA = 0;
    var NBR_OF_VISIBLE_PICS = 7;
    var picArray = null;
    var bigPicObj = new BigPic();
    var scroller = new Scroller();
    var picListObj = new PicList();

    $(document).ready(function(){
        hideAllPics(0);
        hideAllRows();
        updateDropdownMenus();
    });
    $(window).resize(function() {bigPicObj.setBigPicSize();});

    function hideAllPics(delay) {
        picListObj.hidePics(delay);
        bigPicObj.hideBigPic(delay);
    }

    function hideAllRows() {
        $(".pics").fadeOut(0);
        $(".vids").fadeOut(0);
    }

    function selectVids(delay) {
        if( SELECTED_MEDIA == VIDS)
            return;

        $(".active.navig").removeClass("active");
        $("#vids_navig_dd").addClass("active");

        SELECTED_MEDIA = VIDS;
        hideAllPics();
        $(".pics").fadeOut(0);
        $(".vids").fadeIn(0);
    }

    function selectPics(delay) {
        if( SELECTED_MEDIA == PICS)
            return;
        hideAllPics(0);
        $(".active.navig").removeClass("active");
        $("#pics_navig_dd").addClass("active");
        SELECTED_MEDIA = PICS;
        $(".vids").fadeOut(0);
        $(".pics").fadeIn(0);
    }


    /************ MENUS ******************************************/

    function updateDropdownMenus() {
        $.get( "MediaViewer/getimagedirs", function( data ) {
            updatePicsDropDownMenu(data);
        }, "json");
    }

    function updatePicsDropDownMenu(jsonData) {
        if(jsonData.length > 0) {
            $("#pics_navig_dd > ul").children().remove();
            var x;
            for (x in jsonData) {
                var str = jsonData[x];
                var name = str.slice(str.lastIndexOf('/')+1,str.length);
                $("#pics_navig_dd > ul").append(
                        " <li onclick=\"picFolderSelected('"+str+" ')\" dirpath=\"" + str + "\" class=\"pic_selection\"><a href='#' style='color: grey'>" + name +"</a></li>");
            }
        }
    }

    function picFolderSelected(str) {
        selectPics(0);
        $.ajax({
            url: "MediaViewer/getdirimages",
            type: "get",
            data:{directory:  str.trim()},
            dataType: 'json',
            success: function(data) {
                if(data.length > 0) {
                    picArray = data;
                    picListObj.deployPics(data);
                }
            },
            error: function(xhr) {
                alert("error");
                alert(xhr);
            }
        });
    }


    /************ PIC LIST ***************************************/
    function PicList() {
        this.deployPics = function deployPics(jsonData) {
            var hidingTime = 1000;
            if(bigPicObj.imgNotSet())
                hidingTime = 0;
            bigPicObj.hideBigPic(hidingTime);
            this.hidePics(hidingTime);
            setTimeout( function() {
                for (var x in jsonData) {
                    var pic = jsonData[x];
                    var picIndex = parseInt(x,10)+1;
                    if(picIndex > NBR_OF_VISIBLE_PICS)
                        break;
                    $("#pic"+picIndex).attr("src", pic.src);
                    $("#pic"+picIndex).fadeIn(1000);
                }
                bigPicObj.setBigPic(null);
                bigPicObj.fadeInBigImg();
            }, hidingTime);
            showPicsRow(3000);
        };
        this.hidePics = function hidePics(delay) {
            var x = 1;
            for(;x<=NBR_OF_VISIBLE_PICS;x++)
                $("#pic"+x).fadeOut(delay);
        };
    }

    /************ BIG PIC HANDLING *******************************/
    function BigPic() {
        this.bigImg = null;
        this.bigImgSelectedFromPicList = function (src) {
            if(this.bigImg.src != src)
                this.changeBigPic(src);
        };
        this.imgNotSet = function () {
            return this.bigImg == null;
        };
        this.setBigPic = function (src) {
            if(src != null)
                this.bigImg = this.getPicObj(src);
            else
                if( picArray != null)
                    if( picArray.length > 0)
                        this.bigImg = picArray[0];
            $("#BigPic1").attr("src", this.bigImg.src);
            this.setBigPicSize();
        };    
        this.changeBigPic = function (src) {
            this.fadeOutBigImg(600);
            setTimeout( function() {
                bigPicObj.setBigPic(src);
                bigPicObj.fadeInBigImg();
            }, 625);
        };
        this.fadeInBigImg = function () {
            $("#BigPic1").fadeIn(1000);
        };
        this.fadeOutBigImg = function (delay) {
            $("#BigPic1").fadeOut(delay);
            this.bigImg = null;
        };
        this.getPicObj = function (str) {
            for(obj in picArray)
                if(picArray[obj].src === str)
                    return picArray[obj];
        };
        this.setBigPicSize = function () {
            if( picArray == null )
                return;
            if(this.bigImg == null)
                return;
            var maxH = ($(window).height()-70);
            var maxW = $("#div1").prop("clientWidth")-10;
            var aspRatH = maxH / this.bigImg.height;
            var aspRatW = maxW / this.bigImg.width;
            var aspRat = Math.min(aspRatH, aspRatW);
            $("#BigPic1").css("height", this.bigImg.height * aspRat );
            $("#BigPic1").css("width",  this.bigImg.width  * aspRat );
        };
        this.hideBigPic = function hideBigPic(delay) {
            bigPicObj.fadeOutBigImg(delay);
        };
    }

    /************ SCROLL PICS *******************************/
    function Scroller() {
        this.scrollPos = 0;
        this.getInfo = function() {
            return this.color + ' ' + this.type + ' apple';
        };
        this.picsDown = function() {
            this.scrollPos++;
            if(this.scrollPos >= picArray.length)
                this.scrollPos = 0;
            this.scrollPics(this.scrollPos);
        };
        this.picsUp = function() {
            this.scrollPos--;
            if(this.scrollPos < 0)
                this.scrollPos = picArray.length-1;
            this.scrollPics(this.scrollPos);
        };
        this.scrollPics = function (scrollPosition) {
            var x = 1;
            for(; x <= picArray.length; x++)
                if(x<=NBR_OF_VISIBLE_PICS)
                    $("#pic"+x).attr("src", picArray[((scrollPosition+(x-1))%picArray.length)].src);
        };
    }

    /************ EVENT HANDLERS *******************************/
    $('body').bind('mousewheel', function(e){
        if(e.originalEvent.wheelDelta /120 < 0)
            scroller.picsDown();
        else
            scroller.picsUp();
    });
    $(".piclistimg").click(function(){
        bigPicObj.bigImgSelectedFromPicList($(this).attr("src"));
    });
    $( "body" ).keydown(function( event ) {
      if ( event.which == 33 || event.which == 38 )
         scroller.picsUp();
      if ( event.which == 34 || event.which == 40 )
         scroller.picsDown();
    });
