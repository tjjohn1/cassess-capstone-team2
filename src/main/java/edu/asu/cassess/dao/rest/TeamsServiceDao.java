package edu.asu.cassess.dao.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import edu.asu.cassess.persist.entity.rest.RestResponse;
import edu.asu.cassess.persist.entity.rest.Student;
import edu.asu.cassess.persist.entity.rest.Team;
import edu.asu.cassess.persist.entity.taiga.Slugs;
import edu.asu.cassess.persist.entity.taiga.TeamNames;
import edu.asu.cassess.persist.repo.rest.TeamRepo;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Component
@Transactional
public class TeamsServiceDao {

    @Autowired
    private TeamRepo teamDao;

    @Autowired
    private StudentsServiceDao studentsService;

    protected EntityManager entityManager;

    public EntityManager getEntityManager() {
        return entityManager;
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public <T> Object create(Team team) {
        System.out.println("Got into create");
        if(teamDao.findOne(team.getTeam_name()) != null){
            return new RestResponse(team.getTeam_name() + " already exists in database");
        }else{
            teamDao.save(team);
            return team;
        }
    }

    @Transactional
    public <T> Object update(Team team) {
        if(teamDao.findOne(team.getTeam_name()) != null){
            teamDao.save(team);
            return team;
        }else{
            return new RestResponse(team.getTeam_name() + " does not exist in database");
        }
    }

    @Transactional
    public <T> Object find(String team_name) {
        Team team = teamDao.findOne(team_name);
        if(team != null){
            return team;
        }else{
            return new RestResponse(team_name + " does not exist in database");
        }
    }

    @Transactional
    public <T> Object delete(String team_name) {
        Team student = teamDao.findOne(team_name);
        if(student != null){
            teamDao.delete(student);
            return new RestResponse(team_name + " has been removed from the database");
        }else{
            return new RestResponse(team_name + " does not exist in the database");
        }
    }

    @Transactional
    public List<Team> listReadAll() throws DataAccessException {
        Query query = getEntityManager().createNativeQuery("SELECT DISTINCT * FROM cassess.teams", Team.class);
        List<Team> resultList = query.getResultList();
        return resultList;
    }

    @Transactional
    public List<Team> listReadByCourse(String course) throws DataAccessException {
        Query query = getEntityManager().createNativeQuery("SELECT DISTINCT * FROM cassess.teams WHERE course = ?1", Team.class);
        query.setParameter(1, course);
        List<Team> resultList = query.getResultList();
        return resultList;
    }

    @Transactional
    public List<Slugs> listGetSlugs(String course) throws DataAccessException {
        Query query = getEntityManager().createNativeQuery("SELECT DISTINCT taiga_project_slug FROM cassess.teams WHERE course = ?1", Slugs.class);
        query.setParameter(1, course);
        List<Slugs> resultList = query.getResultList();
        return resultList;
    }

    @Transactional
    public List<TeamNames> listGetTeamNames(String course) throws DataAccessException {
        Query query = getEntityManager().createNativeQuery("SELECT DISTINCT team_name AS 'team' FROM cassess.teams WHERE course = ?1", TeamNames.class);
        query.setParameter(1, course);
        List<TeamNames> resultList = query.getResultList();
        return resultList;
    }

    @Transactional
    public JSONObject listCreate(List<Team> teams) {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        JSONArray successArray = new JSONArray();
        JSONArray failureArray = new JSONArray();
        for(Team team:teams)
            if(teamDao.findOne(team.getTeam_name()) != null){
                try {
                    failureArray.put(new JSONObject(ow.writeValueAsString(new RestResponse(team.getTeam_name() + " already exists in database"))));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }else{
                List<Student> students = team.getStudents();
                teamDao.save(team);
                studentsService.listCreate(students);
                try {
                    successArray.put(new JSONObject(ow.writeValueAsString(team)));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        JSONObject returnJSON = new JSONObject();
        returnJSON.put("success", successArray);
        returnJSON.put("failure", failureArray);
        return returnJSON;
    }

    @Transactional
    public JSONObject listUpdate(List<Team> teams) {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        JSONArray successArray = new JSONArray();
        JSONArray failureArray = new JSONArray();
        for (Team team:teams) {
            if (teamDao.findOne(team.getTeam_name()) == null) {
                try {
                    failureArray.put(new JSONObject(ow.writeValueAsString(new RestResponse(team.getTeam_name() + " does not exist in database"))));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            } else {
                List<Student> students = team.getStudents();
                teamDao.save(team);
                studentsService.listCreate(students);
                try {
                    successArray.put(new JSONObject(ow.writeValueAsString(team)));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
        JSONObject returnJSON = new JSONObject();
        returnJSON.put("success", successArray);
        returnJSON.put("failure", failureArray);
        return returnJSON;
    }

    @Transactional
    public <T> Object deleteByCourse(String course) {
        Query preQuery = getEntityManager().createNativeQuery("SELECT * FROM cassess.students WHERE course = ?1 LIMIT 1", Team.class);
        preQuery.setParameter(1, course);
        Student student = (Student) preQuery.getSingleResult();
        if(student != null){
            Query query = getEntityManager().createNativeQuery("DELETE FROM cassess.students WHERE course = ?1");
            query.setParameter(1, course);
            query.executeUpdate();
            return new RestResponse("All teams in course " + course + " have been removed from the database");
        }else{
            return new RestResponse("No teams in course " + course + " exist in the database");
        }
    }

}