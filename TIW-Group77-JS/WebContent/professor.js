(function() {
  var pageOrchestrator = new PageOrchestrator();
  var editSession;
  var reportList;
  var courseList;
  var studentList;
  var registers;
  var course;
  var reports;

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
      orchestrator.refresh();
      makeCall("GET",'GoToHome', null,
        function(req){
          if (req.readyState == XMLHttpRequest.DONE) {
            var message = req.responseText;
               if(req.status==200){
                 var courses = JSON.parse(message);
                 var table = document.getElementById("professorcoursesdata");
                 courses.forEach(function(course){
                  var row = document.createElement("tr");
                  var name = document.createElement("td");
                  name.textContent = course.name;
                  row.appendChild(name);

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
                      makeCall("GET","RegisteredStudents?courseID="+course.ID+"&session="+document.getElementById(course.ID).value, null,
                        function(req){
                          if (req.readyState == XMLHttpRequest.DONE) {
                            if(req.status == 200){
                              orchestrator.showRegisteredStudents(req.responseText);
                            }
                            else{
                              document.getElementById("errormessage").textContent = req.responseText;
                            }
                          }
                        }
                      );
                    },false);
                  }
                  else{
                    var noSession = document.createElement("td");
                    noSession.textContent = "There isn't any session for this course";
                    row.appendChild(noSession);
                  }

                  table.appendChild(row);
                 });

               }
               else{
                     document.getElementById("errormessage").textContent = message;
               }
          }
        },false);
    };
  }

  function StudentList(){
    var self = this;
    this.populateList = function(orchestrator){
      if(registers.length==0){
        document.getElementById("nostudents").style.display = "block";
        document.getElementById("yesstudents").style.display = "none";
      }
      else{
        //showing or hiding buttons
        self.checkButtons(orchestrator);

        //populating the list
        document.getElementById("coursename").textContent = course;
        document.getElementById("sessiondate").textContent = registers[0].sessionDate;
        var table = document.getElementById("registeredstudentstable");
        //clearing the table
        while(table.rows.length > 0) {
          table.deleteRow(0);
        }
        registers.forEach((register) => {
          var row = document.createElement("tr");

          var id = document.createElement("td");
          id.textContent = register.studentID;
          row.appendChild(id);

          var surname = document.createElement("td");
          surname.textContent = register.studentSurname;
          row.appendChild(surname);

          var name = document.createElement("td");
          name.textContent = register.studentName;
          row.appendChild(name);

          var mail = document.createElement("td");
          mail.textContent = register.studentMail;
          row.appendChild(mail);

          var degree = document.createElement("td");
          degree.textContent = register.studentDegree;
          row.appendChild(degree);

          var grade = document.createElement("td");
          grade.textContent = register.grade;
          row.appendChild(grade);

          var state = document.createElement("td");
          state.textContent = register.state;
          row.appendChild(state);
          table.appendChild(row);

          if(register.state == "entered" || register.state == "not entered"){
            var editCell = document.createElement("td");
            row.appendChild(editCell);
            var editButton = document.createElement("input");
            editButton.setAttribute("value","EDIT");
            editButton.setAttribute("type","submit");
            editButton.setAttribute("action","#");
            editButton.addEventListener("click",(e) => {
              e.preventDefault();
              orchestrator.showEditGrade(register,course);
            });
            editCell.appendChild(editButton);
          }
        });

        document.getElementById("nostudents").style.display = "none";
        document.getElementById("yesstudents").style.display = "block";
    }
  }

    this.editList = function(message){
      var register = JSON.parse(message);
      var table = document.getElementById("registeredstudentstable");
      var first = table.querySelectorAll('tr');
      first.forEach((row) => {
        if(row.querySelectorAll('td')[0].textContent == register.studentID){
            row.querySelectorAll('td')[5].textContent = register.grade;
        }
      });
      registers.forEach((reg) => {
        if(reg.studentID == register.studentID && reg.sessionDate == register.sessionDate && reg.courseID == register.courseID){
          reg.grade = register.grade;
        }
      });
    }

    this.checkButtons = function(orchestrator){

      var publishedRegisters = registers.filter(function (register) {
        return (register.state == "published" || register.state == "rejected");
      });
      var notPublishedRegisters = registers.filter(function (register) {
        return register.state == "entered";
      });
      var notEnteredRegisters = registers.filter(function (register) {
        return register.state == "not entered";
      });

      //report button
      if(reports.length>0){
        document.getElementById("reportform").style.display = "block";
        var window = document.getElementById("reportwindow").querySelectorAll('option');
        window.forEach((op) => {
          document.getElementById("reportwindow").removeChild(op);
        });
        reports.forEach((report) => {
          var opt = document.createElement("option");
          opt.textContent = report.date + " " +report.time;
          opt.value = report.ID;
          document.getElementById("reportwindow").appendChild(opt);
        });
        document.getElementById("reportbutton").addEventListener("click",(e) => {
          e.preventDefault();
          var id = document.getElementById("reportwindow").value;
          makeCall("GET","ReportStudentList?reportID="+id,null, function(req){
            if (req.readyState == XMLHttpRequest.DONE) {
              var message = JSON.parse(req.responseText);
                 if(req.status==200){
                   orchestrator.showReport(id,message);
                 }
                 else{
                    document.getElementById("errormessage").textContent = message;
                 }
               }
          },false);
        });
      }else{
        document.getElementById("reportform").style.display = "none";
      }

      //verbalizeButton
      if(publishedRegisters.length>0) {
        document.getElementById("verbalizeform").style.display = "block";
        document.getElementById("courseidverbalize").value = publishedRegisters[0].courseID;
        document.getElementById("sessiondateverbalize").value = publishedRegisters[0].sessionDate;
        document.getElementById("verbalizebutton").addEventListener("click",(e) => {
          e.preventDefault();
          makeCall("POST","Verbalize",e.target.closest("form"),function(req){
            if (req.readyState == XMLHttpRequest.DONE) {
              var message = req.responseText;
                 if(req.status==200){
                    var rep = JSON.parse(message.split("___")[0]);
                    reports.push(rep);
                    orchestrator.showReport(rep.ID,JSON.parse(message.split("___")[1]));                   
                 }
                 else{
                   document.getElementById("errormessage").textContent = JSON.parse(message);
                 }
               }
          },
      false);
        });
      }
      else{
        document.getElementById("verbalizeform").style.display = "none";
      }

      //publishButton
      if(notPublishedRegisters.length>0){
        document.getElementById("publishform").style.display = "block";
        document.getElementById("courseidpublish").value = notPublishedRegisters[0].courseID;
        document.getElementById("sessiondatepublish").value = notPublishedRegisters[0].sessionDate;
        document.getElementById("publishbutton").addEventListener("click",(e) => {
          e.preventDefault();
          makeCall("POST","PublishGrade",e.target.closest("form"),function(req){
                    if (req.readyState == XMLHttpRequest.DONE) {
                      var message = req.responseText;
                         if(req.status==200){
                          orchestrator.showRegisteredStudents(message);
                         }
                         else{
                           document.getElementById("errormessage").textContent = JSON.parse(message);
                         }
                       }
                  },
          false);
        });
      }
      else{
        document.getElementById("publishform").style.display = "none";
      }

      if(notEnteredRegisters.length>0){
        document.getElementById("multipleedit").style.display = "block";
        document.getElementById("modalformcourseid").value = notEnteredRegisters[0].courseID;
        document.getElementById("modalformsessiondate").value = notEnteredRegisters[0].sessionDate;
        document.getElementById("multipleedit").addEventListener("click",(e) => {
          t = document.getElementById("modalformtable");
          var new_t = document.createElement('tbody');
          new_t.setAttribute("id","modalformtable");
          t.parentNode.replaceChild(new_t, t);
          
          e.preventDefault();
          var modaltable = document.getElementById("modalformtable");
          notEnteredRegisters.forEach((register) => {
            var row = document.createElement("tr");
            modaltable.appendChild(row);
            var id = document.createElement("td");
            id.textContent = register.studentID;
            row.appendChild(id);
            var name = document.createElement("td");
            name.textContent = register.studentName;
            row.appendChild(name);
            var surname = document.createElement("td");
            surname.textContent = register.studentSurname;
            row.appendChild(surname);
            var studentgrade = document.createElement("td");
            var f = document.createElement("form");
            studentgrade.appendChild(f);
            var s = document.createElement("select");
            f.appendChild(s);

            var o0 = document.createElement("option");
            o0.value = 0;
            o0.textContent = "";
            s.appendChild(o0);

            var o1 = document.createElement("option");
            o1.value = 1;
            o1.textContent = "absent";
            s.appendChild(o1);

            var o2 = document.createElement("option");
            o2.value = 2;
            o2.textContent = "failing grade";
            s.appendChild(o2);

            var o3 = document.createElement("option");
            o3.value = 3;
            o3.textContent = "skip next session";
            s.appendChild(o3);

            for(var i=18; i<=30; i++){
              var o = document.createElement("option");
              o.value = i;
              o.textContent = i;
              s.appendChild(o);
            }

            var o31 = document.createElement("option");
            o31.value = 31;
            o31.textContent = "30 with honors";
            s.appendChild(o31);

            row.appendChild(studentgrade);
          });
          document.getElementById("modalform").style.display = "block";
          document.getElementById("cancel").addEventListener("click",(e) => {
            e.preventDefault();
            document.getElementById("modalform").style.display = "none";
          });
          document.getElementById("modalformsubmit").addEventListener("click",(e) => {
            e.preventDefault();
            var studentsGrade = new Array();
            var trs = modaltable.querySelectorAll("tr");
            trs.forEach((tr) => {
              var td = tr.querySelectorAll("td")[3];
              var val = td.querySelectorAll("select")[0].value;
              if(val !== "0"){
                studentsGrade.push(tr.querySelectorAll("td")[0].textContent);
                studentsGrade.push(val);
              }
            });
            document.getElementById("modalstudentgrades").value = JSON.stringify(studentsGrade);
            makeCall("POST","EditMultipleGrade",e.target.closest("form"),function(req){
              if (req.readyState == XMLHttpRequest.DONE) {
                var message = req.responseText;
                  if(req.status==200){
                    document.getElementById("modalform").style.display = "none";
                    for(var j=0;j<studentsGrade.length;j=j+2){
                      reg = registers.find(function(r){
                        return r.studentID == studentsGrade[j];
                      });
                      reg.state = "entered";
                      reg.grade = toGrade(studentsGrade[j+1]);
                    }
                    self.populateList(orchestrator);
                  }
                  else{
                    document.getElementById("errormessage").textContent = JSON.parse(message);
                  }
              }
            },false);
          });
        });
      }
      else{
        document.getElementById("multipleedit").style.display = "none";
      }
    }

  };

  toGrade = function(grade){
    switch (grade) {
      case "1": return "absent";
      case "2": return "failing grade";
      case "3": return "skip next session";
      case "31": return "30 with honors";
      default: return grade;
    }
  }

  function ReportList(){
    this.populateReport = function(ID, students){
      var report = reports.filter(function (r) {
        return (r.ID == ID);
      });
      document.getElementById("reportid").textContent = ID;
      document.getElementById("reportdate").textContent = report[0].date;
      document.getElementById("reporttime").textContent = report[0].time;
      document.getElementById("reportcoursename").textContent = course;
      document.getElementById("reportsessiondate").textContent = report[0].dateSession;
      document.getElementById("reportid").textContent = ID;
      var table = document.getElementById("verbalizedstudents");
      //clearing the table
      while(table.rows.length > 0) {
        table.deleteRow(0);
      }
      students.forEach((student) => {
          var row = document.createElement("tr");
          var n = document.createElement("td");
          n.textContent = student.studentName;
          row.appendChild(n);
          var s = document.createElement("td");
          s.textContent = student.studentSurname;
          row.appendChild(s);
          var i = document.createElement("td");
          i.textContent = student.studentID;
          row.appendChild(i);
          var g = document.createElement("td");
          g.textContent = student.grade;
          row.appendChild(g);
          table.appendChild(row);
      });
    }
  };

  function EditSession(){
    var self = this;
    this.populateEdit = function(orchestrator,register,course){
      document.getElementById("studentid").textContent = register.studentID;
      document.getElementById("studentname").textContent = register.studentName;
      document.getElementById("studentemail").textContent = register.studentMail;
      document.getElementById("studentsurname").textContent = register.studentSurname;
      document.getElementById("studentdegree").textContent = register.studentDegree;
      document.getElementById("coursenameedit").textContent = course;
      document.getElementById("sessiondateedit").textContent = register.sessionDate;
      document.getElementById("currentgrade").textContent = register.grade;
      document.getElementById("inputcourseid").value = register.courseID;
      document.getElementById("inputstudentid").value = register.studentID;
      document.getElementById("inputsessionid").value = register.sessionDate;
      document.getElementById("inputsubmit").addEventListener("click",(e) => {
        e.preventDefault();
        makeCall("POST","EditGrade",e.target.closest("form"), function(req){
          if (req.readyState == XMLHttpRequest.DONE) {
            var message = req.responseText;
               if(req.status==200){
                 studentList.editList(message);
               }
               else{
                 document.getElementById("errormessage").textContent = JSON.parse(message);
               }
               orchestrator.showRegisteredStudents(null);
             }
        },false);
      });
    }
  }

  function PageOrchestrator(){
    //builds the first screen and attaches listeners to the buttons
    var self = this;
    this.start = function(){
      document.getElementById("gotohome").addEventListener("click",(e) => {
        e.preventDefault();
        self.showHome();
      });
      reportList = new ReportList();
      courseList = new CourseList();
      studentList = new StudentList();
      editSession = new EditSession();

      var welcomeData = document.getElementById("userdata");
      var user = JSON.parse(sessionStorage.getItem("user"));
      welcomeData.textContent = user.name + " " + user.surname;
      courseList.populateList(this);
    }

    this.refresh = function(){
      registers = null;
      course = null;
      reports = null;
    }

    this.hideAll = function(){
      document.getElementById("welcomeprofessor").style.display = "none";
      document.getElementById("registeredstudents").style.display = "none";
      document.getElementById("editsession").style.display = "none";
      document.getElementById("showreport").style.display = "none";
    }

    this.showHome = function(){
      this.refresh();
      this.hideAll();
      document.getElementById("welcomeprofessor").style.display = "block";
    }

    this.showRegisteredStudents = function(message){
      this.hideAll();
      document.getElementById("registeredstudents").style.display = "block";
      if(message !== null){
        if(message.split("___").length>1){
          registers = JSON.parse(message.split("___")[0]);
          course = JSON.parse(message.split("___")[1]);
          reports = JSON.parse(message.split("___")[2]);
        }
        else{
          table = document.getElementById("registeredstudentstable");
          registers = JSON.parse(message);
          }
        studentList.populateList(this);
      }
    }

    this.showEditGrade = function(register,course){
      this.hideAll();
      document.getElementById("editsession").style.display = "block";
      editSession.populateEdit(this,register,course);
    }

    this.showReport = function(ID,students){
      this.hideAll();
      document.getElementById("showreport").style.display = "block";
      reportList.populateReport(ID,students);
    }
  }

})();
