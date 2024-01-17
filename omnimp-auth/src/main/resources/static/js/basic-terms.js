	/**
	 * 약관 상세보기 링크 공통
	 * 동적 스크립트에서는 동작하지 않으니
	 * 별도로 구성해야함.
	 */
	$(document).ready(function() {
		
		/**
		 * 약관 상세보기 링크
		 */
		$('.btn_link').on('click', function(e) {
			if (typeof $(this).data('lnk') === 'undefined') { return; }
			$('#agree-contents').empty(); 
			var data = {
				tcatCd:$(this).data('lnkcd') + '',
				tncvNo:$(this).data('lnkno') + '',
				chCd:$(this).data('chcd') + '',
				tncTxtUrl:$(this).data('lnk') + '' 
			};
			if (data.tncTxtUrl === '') { return; }
			/*
			var type = '';
			if (data.tcatCd.indexOf('01') > 0) {
				type = 'use';
			} else if (data.tcatCd.indexOf('02') > 0) {
				type = 'per';
			} else if (data.tcatCd.indexOf('03') > 0) {
				type = 'per1';
			}
			$.ajax({
				url:OMNIEnv.ctx + '/search/terms-detail?type=' + type + '&chCd=' + OMNIData.chCd,
				type:'get',
				dataType:'html',
				success: function(html) {
					//$('#agree-contents').html(data);
					var poptitle = '서비스이용약관';
					if (data.tcatCd.indexOf('01') > 0) {
						poptitle = '서비스이용약관';
					} else if (data.tcatCd.indexOf('02') > 0) {
						poptitle = '개인정보 제공 동의';
					} else if (data.tcatCd.indexOf('03') > 0) {
						poptitle = '개인정보 수집/이용 동의';
					}
			 		OMNI.popup.open({
						id:'agress-popup',
					 	title:poptitle,
					 	scroll:true,
					 	content: html,
					 	closelabel:'닫기',
						closeclass:'btn_blue'
			 		});	
				},
				error: function() {
				}
			});*/
			
			$('#agree-contents').load(OMNIEnv.ctx + '/search/agree-detail .agree-txt', data, function(response, status, xhr) {
				if (status === 'success') {
					$('html, body').css('overflow-y', 'hidden');
					var poptitle = '서비스이용약관';
					if (data.tcatCd.indexOf('01') > 0) {
						poptitle = '서비스이용약관';
					} else if (data.tcatCd.indexOf('02') > 0) {
						poptitle = '개인정보 수집/이용 동의';
					} else if (data.tcatCd.indexOf('03') > 0) {
						poptitle = '개인정보 수집/이용 동의';
					} else if (data.tcatCd.indexOf('010') > -1) {
						poptitle = '서비스이용약관';
					} else if (data.tcatCd.indexOf('020') > -1) {
						poptitle = '개인정보 수집 동의';
					} else if (data.tcatCd.indexOf('030') > -1) {
						poptitle = '개인정보 수집/이용 동의';
					} else if (data.tcatCd.indexOf('050') > -1) {
						poptitle = '개인정보 수집 및 이용 동의(마케팅)';
					}
			 		OMNI.popup.open({
						id:'agress-popup',
					 	title:poptitle,
					 	scroll:true,
					 	content: $(this).html(),
					 	closelabel:'닫기',
						closeclass:'btn_blue',
						close:function() {
							$('html, body').css('overflow-y', 'auto');
							OMNI.popup.close({id:'agress-popup'});
							let closefocus = document.querySelectorAll('a');
							for(let elem of closefocus){
								if(elem.matches('a[data-lnkcd$='+data.tcatCd+']')){
									console.log(data.tcatCd);
									elem.focus();
								}
							}
							if (authTimer) {authTimer.stop();}
							if (authRetryTimer) {authRetryTimer.stop();}
						}
			 		});
			 		$('.agree-txt').focus();
				}
			});
			e.preventDefault();
		});
		
		/**
		 * 뷰티포인트 약관 상세보기
		 */
		$('.btn_link_bp').on('click', function() {
			$('#agree-contents').empty();
			var data = { 
				type:$(this).data('type') + '',
				chCd:$(this).data('chcd') + '' 
			};
			if (data.type === '') { return; }
			if (data.chCd === 'undefined') {
				data.chCd = ''
			}
			$('#agree-contents').load(OMNIEnv.ctx + '/omni-terms-detail', data, function(response, status, xhr) {
				if (status === 'success') {
					$('html, body').css('overflow-y', 'hidden');
					var poptitle = '서비스이용약관';
					if (data.type === 'C') {
						poptitle = '개인정보 수집 이용 동의';
					} else if (data.type === 'P' && data.chCd !== '039') {
						poptitle = '개인정보 제공동의';
					} else if (data.type === 'P' && data.chCd === '039') {
						poptitle = '개인정보 제3자 제공 동의';
					} else if (data.type === 'T') {
						poptitle = '국외이전 동의';
					} else if (data.type === 'A') {
						poptitle = '개인정보 제3자 제공 동의';
					} else if (data.type === 'CO') {
						poptitle = '개인정보 수집 이용 동의';
					} else if (data.type === 'O') {
						poptitle = '개인정보 제공동의 (구매내역)';
					} else if (data.type === 'M') {
						poptitle = '개인정보 수집 및 이용 동의(마케팅)';
					}else if (data.type === 'N') {
						poptitle = '개인정보 제공동의 (CRM)';
					}
				 	OMNI.popup.open({
						id:'agress-popup',
					 	title:poptitle,
					 	scroll:true,
					 	content: $(this).html(),
					 	closelabel:'닫기',
						closeclass:'btn_blue',
						close:function() {
							$('html, body').css('overflow-y', 'auto');
							OMNI.popup.close({id:'agress-popup'});
							let closefocus = document.querySelectorAll('a');
							for(let elem of closefocus){
								if(elem.matches('a[data-type$='+data.type+']')){
									elem.focus();
								}
							}
							if (authTimer) {authTimer.stop();}
							if (authRetryTimer) {authRetryTimer.stop();}
						}
				 	});	
				 	$('.agree-txt').focus();
				}
			});
		});		
		
	});