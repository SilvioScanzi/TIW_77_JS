(function() {

  function getCellValue(tr, idx) {
    return tr.children[idx].textContent;
  }

  function createComparer(idx, asc) {
    return function(a, b) {
      var v1 = getCellValue(asc ? a : b, idx),
        v2 = getCellValue(asc ? b : a, idx);
      if (v1 === '' || v2 === '' || isNaN(v1) || isNaN(v2)) {
        return v1.toString().localeCompare(v2);
      }
      return v1 - v2;
    };
  }

  function gradeComparer(idx, asc) {
    return function(a, b) {
      var v1 = getCellValue(asc ? a : b, idx),
        v2 = getCellValue(asc ? b : a, idx);
      if (v1 === '' || v2 === '') {
        return v1.toString().localeCompare(v2);
      }
      if(v1=="30 with honors"){
        v1 = "31";
      }
      if(v2=="30 with honors"){
        v2 = "31";
      }
      if(isNaN(v1) || isNaN(v2)){
        if(isNaN(v1) && isNaN(v2)) return v1.toString().localeCompare(v2);
        else if(isNaN(v1)) return -1;
        else return 1;
      }
      else if(v1>="18" && v1<="31" && v2>="18" && v2<="31"){
        return v1 - v2;
      }
    };
  }

  document.querySelectorAll('button.sortable').forEach(function(button) {
    button.addEventListener('click', function () {
      var table = button.closest('table');
      if(Array.from(button.parentNode.parentNode.children).indexOf(button.parentNode) !== 5){
        Array.from(table.querySelectorAll('tbody > tr'))
        .sort(createComparer(Array.from(button.parentNode.parentNode.children).indexOf(button.parentNode), this.asc = !this.asc))
        .forEach(function(tr) {
          table.querySelector('tbody').appendChild(tr);
        });
      }
      else{
        Array.from(table.querySelectorAll('tbody > tr'))
        .sort(gradeComparer(Array.from(button.parentNode.parentNode.children).indexOf(button.parentNode), this.asc = !this.asc))
        .forEach(function(tr) {
          table.querySelector('tbody').appendChild(tr);
        });
      }
    });
  });
})();
