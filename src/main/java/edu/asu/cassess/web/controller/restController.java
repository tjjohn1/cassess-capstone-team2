package edu.asu.cassess.web.controller;

import edu.asu.cassess.dao.taiga.*;
import edu.asu.cassess.persist.entity.rest.*;
import edu.asu.cassess.persist.entity.security.User;
import edu.asu.cassess.persist.repo.UserRepo;
import edu.asu.cassess.service.rest.*;

import edu.asu.cassess.service.security.IUserService;
import edu.asu.cassess.service.taiga.IMembersService;
import edu.asu.cassess.service.taiga.IProjectService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@SuppressWarnings("SpringJavaAutowiringInspection")
@RestController
@RequestMapping(value = "/rest")
@Api(description = "Nicest Provisioning API")
public class restController {

    @EJB
    private ICourseService courseService;

    @EJB
    private ITeamsService teamService;

    @EJB
    private IStudentsService studentService;

    @EJB
    private IAdminsService adminService;

    @EJB
    private IUserService usersService;

    @EJB
    private IChannelService channelService;

    @Autowired
    private UserRepo userRepo;

    @EJB
    private ITaskTotalsQueryDao taskTotalsDao;

    @EJB
    private IProjectQueryDao projectDao;

    @EJB
    private IMemberQueryDao memberDao;

    @EJB
    private IMembersService members;

    @EJB
    private IProjectService projects;


//-----------------------

    //New CoursePackage REST API Operations
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/coursePackage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object newCoursePackage(@RequestBody Course coursePackage, HttpServletRequest request, HttpServletResponse response) {

        if (coursePackage == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            usersService.createUsersByAdmins(coursePackage.getAdmins());
            for (Team team : (coursePackage.getTeams())) {
                usersService.createUsersByStudents(team.getStudents());
            }
            Object object = courseService.create(coursePackage);
            projects.updateProjects(coursePackage.getCourse());
            members.updateMembership(coursePackage.getCourse());
            return object;
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/coursePackage", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object updateCoursePackage(@RequestBody Course coursePackage, HttpServletRequest request, HttpServletResponse response) {

        if (coursePackage == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            usersService.courseUpdate(coursePackage);
            projects.updateProjects(coursePackage.getCourse());
            members.updateMembership(coursePackage.getCourse());
            return courseService.update(coursePackage);
        }
    }


    //New Student REST API Operations
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/student", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object deleteStudent(@RequestBody Student student, HttpServletRequest request, HttpServletResponse response) {
        if(student != null){
            response.setStatus(HttpServletResponse.SC_OK);
            User user = userRepo.findByEmail(student.getEmail());
            if(user != null)
            {
                usersService.deleteUser(user);
            }
            taskTotalsDao.deleteTaskTotalsByStudent(student);
            memberDao.deleteMembersByStudent(student);
            return studentService.delete(student);
        }else{
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
    }



    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/student", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object updateStudent(@RequestBody Student student, HttpServletRequest request, HttpServletResponse response) {
        if (student == null) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            User user = userRepo.findByEmail(student.getEmail());
            if(user != null)
            {
                usersService.updateStudent(student, user);
            }
            members.updateMembership(student.getCourse());
            return studentService.update(student);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/list/student", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> List<Student> getStudentList(HttpServletRequest request, HttpServletResponse response) {

            response.setStatus(HttpServletResponse.SC_OK);
            return studentService.listReadAll();
        }

    //New Course REST API Operations
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/course", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object deleteCourse(@RequestBody Course courseInput, HttpServletRequest request, HttpServletResponse response) {

        Object object = new Object();
        if (courseInput == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } else {
            Course course = (Course) courseService.read(courseInput.getCourse());
            if(course != null){
                usersService.courseDelete(course);
                taskTotalsDao.deleteTaskTotalsByCourse(course);
                projectDao.deleteTaigaProjectByCourse(course);
                memberDao.deleteMembersByCourse(course);
                object = courseService.delete(course);

            }
        }
        response.setStatus(HttpServletResponse.SC_OK);
        return object;
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/course", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object updateCourse(@RequestBody Course course, HttpServletRequest request, HttpServletResponse response) {

        if (course == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            usersService.courseUpdate(course);
            projects.updateProjects(course.getCourse());
            members.updateMembership(course.getCourse());
            return courseService.update(course);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/list/course", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object readCourseList(HttpServletRequest request, HttpServletResponse response) {

            response.setStatus(HttpServletResponse.SC_OK);

            return courseService.listRead();
        }

    //New Slack Channel REST API Operations
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/slack_channel", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object deleteChannel(@RequestBody Channel channel, HttpServletRequest request, HttpServletResponse response) {
        if (channel == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);

            return channelService.delete(channel);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/slack_channel", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object updateChannel(@RequestBody Channel channel, HttpServletRequest request, HttpServletResponse response) {
        if (channel == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            return channelService.update(channel);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/list/slack_channel", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object updateChannelList(@RequestBody List<Channel> channels, HttpServletRequest request, HttpServletResponse response) {
        if (channels == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);

            return channelService.listUpdate(channels);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/list/slack_channel", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object readChannelList(HttpServletRequest request, HttpServletResponse response) {
            response.setStatus(HttpServletResponse.SC_OK);
            return channelService.listRead();
        }

    //New Admin REST API Operations
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/admin", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object deleteAdmin(@RequestBody Admin admin, HttpServletRequest request, HttpServletResponse response) {
        if (admin == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            User user = userRepo.findByEmail(admin.getEmail());
            if(user != null)
            {
                usersService.deleteUser(user);
            }
            return adminService.delete(admin);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/admin", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object updateAdmin(@RequestBody Admin admin, HttpServletRequest request, HttpServletResponse response) {
        if (admin == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            User user = userRepo.findByEmail(admin.getEmail());
            if (user != null)
            {
                usersService.updateAdmin(admin, user);
            }
            return adminService.update(admin);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/list/admin", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object updateAdmin(@RequestBody List<Admin> admins, HttpServletRequest request, HttpServletResponse response) {
        if (admins == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            for(Admin admin:admins){
                User user = userRepo.findByEmail(admin.getEmail());
                if (user != null)
                {
                    usersService.updateAdmin(admin, user);
                }
            }
            return adminService.listUpdate(admins);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/list/admin", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object readAdminList(HttpServletRequest request, HttpServletResponse response) {
            response.setStatus(HttpServletResponse.SC_OK);
            return adminService.listReadAll();
        }


    //New Team REST API Operations
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/team", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object deleteTeam(@RequestBody Team teamInput, HttpServletRequest request, HttpServletResponse response) {

        if (teamInput == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } else {
            Team team = (Team) teamService.read(teamInput.getTeam_name(), teamInput.getCourse());
            response.setStatus(HttpServletResponse.SC_OK);
            if(team != null){
                usersService.teamDelete(team);
                taskTotalsDao.deleteTaskTotalsByProject(team);
                projectDao.deleteTaigaProjectByTeam(team);
                memberDao.deleteMembersByTeam(team);
            }

            return teamService.delete(team);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/team", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object updateTeam(@RequestBody Team team, HttpServletRequest request, HttpServletResponse response) {

        if (team == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            usersService.teamUpdate(team);
            members.updateMembership(team.getCourse());
            projects.updateProjects(team.getCourse());
            return teamService.update(team);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/list/team", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object updateTeamList(@RequestBody List<Team> teams, HttpServletRequest request, HttpServletResponse response) {

        if (teams == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            for(Team team:teams){
                usersService.teamUpdate(team);
                projects.updateProjects(team.getCourse());
                members.updateMembership(team.getCourse());
            }
            return teamService.listUpdate(teams);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/list/team", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    <T> Object readTeamList(HttpServletRequest request, HttpServletResponse response) {

            response.setStatus(HttpServletResponse.SC_OK);
            return teamService.listReadAll();
        }




}

