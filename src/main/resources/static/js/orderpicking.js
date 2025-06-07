$(document).ready(function () {
  const serialInput = $('input[name="scannedSerial"]');
  serialInput.focus();

  serialInput.on('keypress', function (e) {
    if (e.which === 13) {
      e.preventDefault();
      $('#saveButton').click();
    }
  });

  $('#saveButton').on('click', function () {
    console.log("Serial vừa quét:", serialInput.val());
    serialInput.val('').focus();
  });

  // Toggle các dòng con
 	
  $('.toggle-detail-btn').on('click', function () {
    const idx = $(this).data('index'); // Lấy index của dòng cha
    const $icon = $(this).find('i'); // Lấy icon dấu cộng hoặc trừ
    const $parentRow = $(`.parent-row-${idx}`); // Dòng cha
    const $detailRows = $(`.detail-group-${idx}`); // Các dòng con (items)
    const $showMoreRow = $(`#show-more-row-${idx}`); // Dòng "Hiển thị thêm..."

    if ($icon.hasClass('fa-plus')) {
		/* ------------------------------------------------------------- */
	      $icon.removeClass('fa-plus').addClass('fa-minus');
	      $detailRows.each(function (i) {
	        if (i < 6) {
	          $(this).removeClass('d-none');  // Hiển thị 6 dòng đầu tiên
	        } else {
	          $(this).removeClass('d-none').addClass('extra-item d-none');  // Các dòng vượt quá 6 sẽ ẩn
	        }
	      });
	
	      // Nếu có hơn 6 dòng con, hiện dòng "Hiển thị thêm..."
	      if ($detailRows.length > 6) {
	        $showMoreRow.removeClass('d-none');
	      }
	      $parentRow.addClass('active-parent-row');
	  	/* ------------------------------------------------------------- */
	  
    }  else if ($icon.hasClass('fa-minus')) {
      $icon.removeClass('fa-minus').addClass('fa-plus');
	  
      $detailRows.each(function () {
        $(this).removeClass('extra-item').addClass('d-none');  // Chỉ ẩn các dòng con, không xóa khỏi DOM
      });
      $showMoreRow.addClass('d-none');
      $parentRow.removeClass('active-parent-row');
    } 
  });




  
  
  
  
  // Xử lý nút "Hiển thị thêm..."
  $('.show-more-link').on('click', function () {
    const idx = $(this).data('index');
    const $detailRows = $(`.detail-group-${idx}`);
    const $showMoreRow = $(`#show-more-row-${idx}`);

    // Lấy các dòng đã được hiển thị và các dòng chưa được hiển thị
    const remainingRows = $(`.detail-group-${idx}.d-none`);

    // Hiển thị 6 dòng tiếp theo
    remainingRows.slice(0, 6).removeClass('d-none');

    // Kiểm tra nếu không còn dòng nào để hiển thị nữa
    if (remainingRows.length <= 6) {
      $showMoreRow.addClass('d-none'); // Ẩn "Hiển thị thêm..." khi không còn dòng nữa
    }
  });

  console.log("✅ orderpicking.js đã load xong");
});
