(function() {
  var pageOrchestrator = new PageOrchestrator();
  //onload checks user and shows welcome message
  window.addEventListener("load", () => {
    if (sessionStorage.getItem("user") == null) {
      window.location.href = "index.html";
    } else {
      pageOrchestrator.start();
      pageOrchestrator.showHome();
    }
  }, false);

  function CourseList(){
    this.populateList = function(orchestrator){
      makeCall("GET",'GoToHome', null,
        function(req){
          if (req.readyState == XMLHttpRequest.DONE) {
            var message = req.responseText;
               if(req.status==200){
                 var courses = JSON.parse(message);
                 var table = document.getElementById("studentcoursesdata");
                 courses.forEach(function(course){
                  var row = document.createElement("tr");
                  var name = document.createElement("td");
                  name.textContent = course.name;
                  row.appendChild(name);

                  var prof = document.createElement("td");
                  prof.textContent = course.profName;
                  row.appendChild(prof);

                  if(course.session.length > 0){
                    var sessionData = document.createElement("td");
                    var sessionForm = document.createElement("form");
                    sessionForm.action = "#";
                    var sessionSelect = document.createElement("select");
                    sessionSelect.setAttribute("id",course.ID);
                    sessionSelect.setAttribute("name","session");
                    course.session.forEach(function(sessionDate){
                      var sessionOption = document.createElement("option");
                      sessionOption.value = sessionDate;
                      sessionOption.textContent = sessionDate;
                      sessionSelect.appendChild(sessionOption);
                    });
                    sessionForm.appendChild(sessionSelect);
                    sessionData.appendChild(sessionForm);

                    var selectButton = document.createElement("button");
                    selectButton.textContent = "select session";
                    sessionForm.appendChild(selectButton);
                    row.appendChild(sessionData);

                    selectButton.addEventListener("click",(e) => {
                      e.preventDefault();
                      makeCall("GET","SessionResult?courseID="+course.ID+"&session="+document.getElementById(course.ID).value, null,
                        function(req){
                          msg = req.responseText
                          if (req.readyState == XMLHttpRequest.DONE) {
                            if(req.status == 200){
                              orchestrator.showSessionResult(msg);
                            }
                            else{
                              document.getElementById("errormessage").textContent = msg;
                            }
                          }
                        }
                      );
                    },false);
                  }
                  else{
                    var noSession = document.createElement("td");
                    noSession.textContent = "You aren't registered in any session of this course";
                    row.appendChild(noSession);
                  }

                  table.appendChild(row);
                 });

               }
               else{
                     document.getElementById("errormessage").textContent = message;
               }
          }
        });
    };
  }

  function SessionList(){
    var self = this;
    this.populateList = function(message){
      var course = JSON.parse(message.split("___")[0]);
      var register = JSON.parse(message.split("___")[1]);
      if(register.state == "entered" || register.state == "not entered"){
        document.getElementById("nograde").style.display = "block";
        document.getElementById("yesgrade").style.display = "none";
      }
      else{
        document.getElementById("studentid").textContent = register.studentID;
        document.getElementById("studentname").textContent = JSON.parse(sessionStorage.getItem("user")).name;
        document.getElementById("studentsurname").textContent = JSON.parse(sessionStorage.getItem("user")).surname;
        document.getElementById("degree").textContent = JSON.parse(sessionStorage.getItem("user")).degree;
        document.getElementById("coursename").textContent = course.name;
        document.getElementById("profname").textContent = course.profName;
        document.getElementById("date").textContent = register.sessionDate;
        document.getElementById("grade").textContent = register.grade;
        document.getElementById("state").textContent = register.state;
        if(register.state == "rejected"){
          document.getElementById("state").setAttribute("class","red");
          document.getElementById("state").textContent = "Rejected";
        }

        if(register.state == "published" && register.grade !== "failing grade" && register.grade !== "absent" && register.grade !== "skip next session"){
          var form = document.getElementById("rejectform");
          var rejectButton = document.createElement("input");
          rejectButton.setAttribute("value","REJECT");
          rejectButton.setAttribute("type","submit");
          var courseID = document.createElement("input");
          courseID.setAttribute("type","hidden");
          courseID.value = course.ID;
          courseID.setAttribute("name","courseID");
          var session = document.createElement("input");
          session.setAttribute("type","hidden");
          session.value = register.sessionDate;

          session.setAttribute("name","session");
          form.appendChild(rejectButton);
          form.appendChild(session);
          form.appendChild(courseID);
          rejectButton.addEventListener("click",(e) => {
            e.preventDefault();
            makeCall("POST","Reject", e.target.closest("form") ,
              function(req){
                if (req.readyState == XMLHttpRequest.DONE) {
                  if(req.status == 200){
                    self.showReject(req.responseText);
                    rejectButton.remove();
                  }
                  else{
                    document.getElementById("errormessage").textContent = message;
                  }
                }
              }
            );
          },false);
          document.getElementById("rejectform").style.display = "block";
      } else{
        document.getElementById("rejectform").style.display = "none";
      }
        document.getElementById("nograde").style.display = "none";
        document.getElementById("yesgrade").style.display = "block";
    }
  }

    this.showReject = function(message){
      document.getElementById("rejectmessage").textContent = message;
      document.getElementById("state").textContent = "Rejected";
      document.getElementById("state").setAttribute("class","red");
    }
  }

  function PageOrchestrator(){
    //builds the first screen and attaches listeners to the buttons
    var self = this;
    this.start = function(){
      document.getElementById("gotohome").addEventListener("click",(e) => {
        self.showHome();
      });
      sessionList = new SessionList();
      courseList = new CourseList();
    }

    this.hideAll = function(){
      document.getElementById("welcomestudent").style.display = "none";
      document.getElementById("sessionresult").style.display = "none";
    }

    this.showHome = function(){
      this.hideAll();
      document.getElementById("welcomestudent").style.display = "block";
      var welcomeData = document.getElementById("userdata");
      var user = JSON.parse(sessionStorage.getItem("user"));
      welcomeData.textContent = user.name + " " + user.surname;
      courseList.populateList(this);
    }

    this.showSessionResult = function(message){
      this.hideAll();
      document.getElementById("sessionresult").style.display = "block";
      sessionList.populateList(message);
    }
  }
})();
