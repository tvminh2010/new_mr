
/* Kích hoạt DataTables */
$(document).ready(function() {
	$('#materialCoreTable').DataTable({
		language: {
			search: "Tìm kiếm:",
			lengthMenu: "Hiển thị _MENU_ dòng",
			info: "Hiển thị _START_ đến _END_ (trong tổng số _TOTAL_ bản ghi)!",
			paginate: {
				first: "Đầu",
				last: "Cuối",
				next: "Sau",
				previous: "Trước"
			}
		}
	});
});
/* ------------------------------------------------------------------------------------------- */
/*** Xác nhập xóa dữ liệu WorkOrder */
function confirmDelete() {
	return confirm("Bạn có chắc chắn muốn xóa Material core này?");
}


/* ------------------ Submit form for deletion of workOrder ---------------------------------- */
function submitDeleteForm() {
	
    if (confirm("Bạn có chắc muốn xoá không?")) {
        document.getElementById("deleteMaterialCore").submit();
    }
	document.getElementById("deleteMaterialCore").submit();
    return false;
}
/* ------------------ Xóa trắng ---------------------------------- */
function myClear() {
	const form = document.getElementById("MyForm");
	if (form) {
		form.reset();
		form.querySelectorAll("input[type='text']").forEach(input => {
			input.value = "";
		});
	}
}
/* ---------------------------------------------------- */