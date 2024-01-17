(function ($) {
  "use strict";

  var $doc = $(document),
    $win = $(window);

  // layerPop
  var layerPop = function ($win) {
    var $wins = $win || $doc,
      $html = $("html"),
      $wrap = $(".wrap"),
      $btn_popOpen = $wins.find("[data-pop=btn-open-pop]"),
      $btn_popClose = $wins.find("[data-pop=btn-close-pop]"),
      $scrollTop;

    $btn_popOpen.on("click", function () {
      $scrollTop = $(window).scrollTop();
      $html.addClass("is-scr-block");
      $(window).scrollTop(0);
      $wrap.scrollTop($scrollTop);
      var target = $(this).attr("data-target");
      $(target).addClass("is_show");
    });

    $btn_popClose.on("click", function () {
      var target = $(this).attr("data-target");
      $(target).queue(function () {
        $(this).removeClass("is_show");
        $(this).dequeue();
        $html.removeClass("is-scr-block");
        $(window).scrollTop($scrollTop);
        $wrap.scrollTop(0);
      });
    });
  };
  layerPop();

  var inputClear = function () {
    var $inp_text = $(".inp .inp_text"),
      $btn_del = $(".inp .btn_del");

    $($inp_text).each(function () {
      $(this).keyup(function () {
        $(this)
          .parent(".inp")
          .find(".btn_del")
          .toggle(Boolean($(this).val()));
      });
    });

    $($btn_del).each(function () {
      var $target_inp = $(this).parent(".inp").find(".inp_text");
      $(this).toggle(Boolean($target_inp.val()));

      $(this).click(function () {
        $target_inp.val("").focus();
        $(this).hide();
      });
    });
  };
  inputClear();

  var view_keyboard = function () {
    var $keyboardBox = $(".view_keyboard"),
      $btn_keyboard = $keyboardBox.find(".btn_keyboard");

    $($btn_keyboard).on("click", function () {
      var isCheck = $keyboardBox.hasClass("is_open");
      if (!isCheck) {
        $keyboardBox.addClass("is_open");
      } else {
        $keyboardBox.removeClass("is_open");
      }
    });
  };
  view_keyboard();

  var info_notice_toggle = function () {
    var $btn_open_info = $(".btn_open_info"),
      $close_layer_info = $(".close_layer_info");

    $($btn_open_info).on("click", function () {
      var $layer_T = $(this).parent(".info_notice").find(".layer_info"),
        isShowCheck = $layer_T.is(":visible");
      if (!isShowCheck) {
        $layer_T.stop().show();
      } else {
        $layer_T.stop().hide();
      }
    });

    $($close_layer_info).on("click", function () {
      $(this).parent(".layer_info").stop().hide();
    });
  };
  info_notice_toggle();

  var all_agree_view = function () {
    var $btn_all_view = $(".btn_all_view");

    $($btn_all_view).on("click", function () {
      var $all_T = $(this).parents(".all_agree_box "),
        isViewCheck = $all_T.hasClass("is_open");
      if (!isViewCheck) {
        $all_T.addClass("is_open");
      } else {
        $all_T.removeClass("is_open");
      }
    });
  };
  all_agree_view();

  var login_tooltip_close = function () {
    $(".btn_tooltip_close").on("click", function () {
      $(this).parent(".login_tootip").hide();
    });
  };
  login_tooltip_close();
  
})(jQuery);
