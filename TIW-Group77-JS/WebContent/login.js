(function() {

  document.getElementById("loginbutton").addEventListener('click', (e) => {
    var form = e.target.closest("form");
    if (form.checkValidity()) {
      makeCall("POST", 'CheckLogin', form,
        function(req) {
          if (req.readyState == XMLHttpRequest.DONE) {
            var message = req.responseText;
			         if(req.status==200){
                 sessionStorage.setItem("user", message);
                 var user = JSON.parse(message);
                 if(user.role == "student"){
                   window.location.href = "StudentHome.html";
                 }
                 else if(user.role == "professor"){
                   window.location.href = "ProfessorHome.html";
                 }
			         }
			         else{
				             document.getElementById("errormessage").textContent = message;
			         }
          }
        }
      );
    } else {
    	 form.reportValidity();
    }
  });

})();
