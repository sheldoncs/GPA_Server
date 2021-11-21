package net.gpa.db;

import java.rmi.RemoteException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import net.gpa.logger.MessageLogger;
import net.gpa.remote.GPARemoteService;
import net.gpa.util.CourseAndGrade;
import net.gpa.util.DegreeAwarded;
import net.gpa.util.StudentProgram;

public class GPADb extends OracleDBConnection {

	public GPADb() {

		super();

	}

	private String getAllOldDegreeGPASQL() {

		String sqlstmt = "select AS_STUDENT_ENROLLMENT_SUMMARY.ID AS ID, "
				+ "Sum( SFRSTCR.SFRSTCR_CREDIT_HR * SHRGRDE.SHRGRDE_QUALITY_POINTS ) AS Sum_Grade_Points, "
				+ "Sum( SFRSTCR.SFRSTCR_CREDIT_HR ) AS Sum_Credit_Hours, "
				+ "(sum(SFRSTCR.SFRSTCR_CREDIT_HR * SHRGRDE.SHRGRDE_QUALITY_POINTS)/ "
				+ "sum (SFRSTCR.SFRSTCR_CREDIT_HR)) AS GPA "
				+ "from BANINST1.AS_STUDENT_ENROLLMENT_SUMMARY AS_STUDENT_ENROLLMENT_SUMMARY, "
				+ "SATURN.SFRSTCR SFRSTCR, "
				+ "SATURN.SSBSECT SSBSECT, "
				+ " SATURN.SHRGRDE SHRGRDE "
				+ "where ( SFRSTCR.SFRSTCR_TERM_CODE = SSBSECT.SSBSECT_TERM_CODE "
				+ "and SFRSTCR.SFRSTCR_CRN = SSBSECT.SSBSECT_CRN "
				+ "and AS_STUDENT_ENROLLMENT_SUMMARY.TERM_CODE_KEY = SFRSTCR.SFRSTCR_TERM_CODE "
				+ "and AS_STUDENT_ENROLLMENT_SUMMARY.PIDM_KEY = SFRSTCR.SFRSTCR_PIDM "
				+ "and SHRGRDE.SHRGRDE_CODE = SFRSTCR.SFRSTCR_GRDE_CODE "
				+ "and SHRGRDE.SHRGRDE_LEVL_CODE = SFRSTCR.SFRSTCR_LEVL_CODE ) "
				+ "and ( SFRSTCR.SFRSTCR_CREDIT_HR <> ? "
				+ "and SFRSTCR.SFRSTCR_LEVL_CODE = ? "
				+ "and SHRGRDE.SHRGRDE_GPA_IND = ? "
				+ "and SFRSTCR.SFRSTCR_TERM_CODE >= ? "
				+ "and (SSBSECT.SSBSECT_CRSE_NUMB like ? "
				+ "or SSBSECT.SSBSECT_CRSE_NUMB like ?)) "
				+ "group by AS_STUDENT_ENROLLMENT_SUMMARY.ID ";

		return sqlstmt;
	}

	private String getAllAccumGPASQL(boolean isPreviousGPA) {

		String sqlstmt = "";

		if (!isPreviousGPA) {
			sqlstmt = "select AS_STUDENT_ENROLLMENT_SUMMARY.ID AS ID, "
					+ "Sum( SFRSTCR.SFRSTCR_CREDIT_HR * SHRGRDE.SHRGRDE_QUALITY_POINTS ) AS Sum_Grade_Points, "
					+ "Sum( SFRSTCR.SFRSTCR_CREDIT_HR ) AS Sum_Credit_Hours, "
					+ "(sum(SFRSTCR.SFRSTCR_CREDIT_HR * SHRGRDE.SHRGRDE_QUALITY_POINTS)/ "
					+ "sum (SFRSTCR.SFRSTCR_CREDIT_HR)) AS GPA "
					+ "from BANINST1.AS_STUDENT_ENROLLMENT_SUMMARY AS_STUDENT_ENROLLMENT_SUMMARY, "
					+ "SATURN.SFRSTCR SFRSTCR, "
					+ "SATURN.SSBSECT SSBSECT, "
					+ " SATURN.SHRGRDE SHRGRDE "
					+ "where ( SFRSTCR.SFRSTCR_TERM_CODE = SSBSECT.SSBSECT_TERM_CODE "
					+ "and SFRSTCR.SFRSTCR_CRN = SSBSECT.SSBSECT_CRN "
					+ "and AS_STUDENT_ENROLLMENT_SUMMARY.TERM_CODE_KEY = SFRSTCR.SFRSTCR_TERM_CODE "
					+ "and AS_STUDENT_ENROLLMENT_SUMMARY.PIDM_KEY = SFRSTCR.SFRSTCR_PIDM "
					+ "and SHRGRDE.SHRGRDE_CODE = SFRSTCR.SFRSTCR_GRDE_CODE "
					+ "and SHRGRDE.SHRGRDE_LEVL_CODE = SFRSTCR.SFRSTCR_LEVL_CODE ) "
					+ "and ( SFRSTCR.SFRSTCR_CREDIT_HR <> ? "
					+ "and SFRSTCR.SFRSTCR_LEVL_CODE = ? "
					+ "and SHRGRDE.SHRGRDE_GPA_IND = ? "
					+ "and SFRSTCR.SFRSTCR_TERM_CODE >= ? and AS_STUDENT_ENROLLMENT_SUMMARY.ID = ? ) "
					+ "group by AS_STUDENT_ENROLLMENT_SUMMARY.ID ";
		} else if (isPreviousGPA) {

			sqlstmt = "select AS_STUDENT_ENROLLMENT_SUMMARY.ID AS ID, "
					+ "Sum( SFRSTCR.SFRSTCR_CREDIT_HR * SHRGRDE.SHRGRDE_QUALITY_POINTS ) AS Sum_Grade_Points, "
					+ "Sum( SFRSTCR.SFRSTCR_CREDIT_HR ) AS Sum_Credit_Hours, "
					+ "(sum(SFRSTCR.SFRSTCR_CREDIT_HR * SHRGRDE.SHRGRDE_QUALITY_POINTS)/ "
					+ "sum (SFRSTCR.SFRSTCR_CREDIT_HR)) AS GPA "
					+ "from BANINST1.AS_STUDENT_ENROLLMENT_SUMMARY AS_STUDENT_ENROLLMENT_SUMMARY, "
					+ "SATURN.SFRSTCR SFRSTCR, "
					+ "SATURN.SSBSECT SSBSECT, "
					+ " SATURN.SHRGRDE SHRGRDE "
					+ "where ( SFRSTCR.SFRSTCR_TERM_CODE = SSBSECT.SSBSECT_TERM_CODE "
					+ "and SFRSTCR.SFRSTCR_CRN = SSBSECT.SSBSECT_CRN "
					+ "and AS_STUDENT_ENROLLMENT_SUMMARY.TERM_CODE_KEY = SFRSTCR.SFRSTCR_TERM_CODE "
					+ "and AS_STUDENT_ENROLLMENT_SUMMARY.PIDM_KEY = SFRSTCR.SFRSTCR_PIDM "
					+ "and SHRGRDE.SHRGRDE_CODE = SFRSTCR.SFRSTCR_GRDE_CODE "
					+ "and SHRGRDE.SHRGRDE_LEVL_CODE = SFRSTCR.SFRSTCR_LEVL_CODE ) "
					+ "and ( SFRSTCR.SFRSTCR_CREDIT_HR <> ? "
					+ "and SFRSTCR.SFRSTCR_LEVL_CODE = ? "
					+ "and SHRGRDE.SHRGRDE_GPA_IND = ? "
					+ "and (SFRSTCR.SFRSTCR_TERM_CODE >= ? and SFRSTCR.SFRSTCR_TERM_CODE < ?) and AS_STUDENT_ENROLLMENT_SUMMARY.ID = ? ) "
					+ "group by AS_STUDENT_ENROLLMENT_SUMMARY.ID ";

		}
		return sqlstmt;
	}

	private String getGPASQL(boolean isPreviousGPA) {
		String sqlstmt = "";
		if (!isPreviousGPA) {

			sqlstmt = "select distinct SFRSTCR.SFRSTCR_PIDM AS PIDM, "
					+ "Sum( SFRSTCR.SFRSTCR_CREDIT_HR * SHRGRDE.SHRGRDE_QUALITY_POINTS ) AS Sum_Grade_Points, "
					+ "Sum( SFRSTCR.SFRSTCR_CREDIT_HR ) AS Sum_Credit_Hours, "
					+ "(sum(SFRSTCR.SFRSTCR_CREDIT_HR * SHRGRDE.SHRGRDE_QUALITY_POINTS)/ "
					+ "sum (SFRSTCR.SFRSTCR_CREDIT_HR)) AS GPA "
					+ "FROM SATURN.SFRSTCR, SATURN.SPRIDEN, SATURN.SSBSECT, "
					+ "SHRGRDE, BANINST1.SOVCLAS SOVCLAS, SATURN.STVCLAS, SATURN.SGBSTDN "
					+ "WHERE SPRIDEN_PIDM = SFRSTCR_PIDM "
					+ "AND SHRGRDE.SHRGRDE_LEVL_CODE = SFRSTCR.SFRSTCR_LEVL_CODE "
					+ "AND SOVCLAS.SOVCLAS_TERM_CODE =  SFRSTCR_TERM_CODE "
					+ "AND SOVCLAS.SOVCLAS_PIDM = SFRSTCR_PIDM "
					+ "AND SSBSECT_CRN = SFRSTCR_CRN "
					+ "AND SOVCLAS.SOVCLAS_CLAS_CODE = STVCLAS_CODE "
					+ "AND SHRGRDE.SHRGRDE_CODE = SFRSTCR.SFRSTCR_GRDE_CODE "
					+ "AND SGBSTDN_TERM_CODE_EFF = SFRSTCR_TERM_CODE "
					+ "AND SSBSECT_TERM_CODE = SFRSTCR_TERM_CODE "
					+ "AND SHRGRDE.SHRGRDE_LEVL_CODE = SFRSTCR.SFRSTCR_LEVL_CODE "
					+ "AND SGBSTDN_PIDM = SFRSTCR_PIDM "
					+ "AND SFRSTCR.SFRSTCR_CREDIT_HR <> ? "
					+ "AND SFRSTCR.SFRSTCR_LEVL_CODE = ? "
					+ "AND SHRGRDE.SHRGRDE_GPA_IND = ? "
					+ "AND (SFRSTCR_TERM_CODE >= ? " + "AND SPRIDEN_ID = ? "
					+ "and (SSBSECT.SSBSECT_CRSE_NUMB like ? "
					+ "OR SSBSECT.SSBSECT_CRSE_NUMB like ?) "
					+ "group by SFRSTCR.SFRSTCR_PIDM ";

		} else if (isPreviousGPA) {

			sqlstmt = "select distinct SFRSTCR.SFRSTCR_PIDM AS PIDM, "
					+ "Sum( SFRSTCR.SFRSTCR_CREDIT_HR * SHRGRDE.SHRGRDE_QUALITY_POINTS ) AS Sum_Grade_Points, "
					+ "Sum( SFRSTCR.SFRSTCR_CREDIT_HR ) AS Sum_Credit_Hours, "
					+ "(sum(SFRSTCR.SFRSTCR_CREDIT_HR * SHRGRDE.SHRGRDE_QUALITY_POINTS)/ "
					+ "sum (SFRSTCR.SFRSTCR_CREDIT_HR)) AS GPA "
					+ "FROM SATURN.SFRSTCR, SATURN.SPRIDEN, SATURN.SSBSECT, "
					+ "SHRGRDE, BANINST1.SOVCLAS SOVCLAS, SATURN.STVCLAS, SATURN.SGBSTDN "
					+ "WHERE SPRIDEN_PIDM = SFRSTCR_PIDM "
					+ "AND SHRGRDE.SHRGRDE_LEVL_CODE = SFRSTCR.SFRSTCR_LEVL_CODE "
					+ "AND SOVCLAS.SOVCLAS_TERM_CODE =  SFRSTCR_TERM_CODE "
					+ "AND SOVCLAS.SOVCLAS_PIDM = SFRSTCR_PIDM "
					+ "AND SSBSECT_CRN = SFRSTCR_CRN "
					+ "AND SOVCLAS.SOVCLAS_CLAS_CODE = STVCLAS_CODE "
					+ "AND SHRGRDE.SHRGRDE_CODE = SFRSTCR.SFRSTCR_GRDE_CODE "
					+ "AND SGBSTDN_TERM_CODE_EFF = SFRSTCR_TERM_CODE "
					+ "AND SSBSECT_TERM_CODE = SFRSTCR_TERM_CODE "
					+ "AND SHRGRDE.SHRGRDE_LEVL_CODE = SFRSTCR.SFRSTCR_LEVL_CODE "
					+ "AND SGBSTDN_PIDM = SFRSTCR_PIDM "
					+ "AND SFRSTCR.SFRSTCR_CREDIT_HR <> ? "
					+ "AND SFRSTCR.SFRSTCR_LEVL_CODE = ? "
					+ "AND SHRGRDE.SHRGRDE_GPA_IND = ? "
					+ "and SFRSTCR.SFRSTCR_TERM_CODE >= ? AND SFRSTCR.SFRSTCR_TERM_CODE < ? "
					+ "AND SPRIDEN_ID = ? "
					+ "and (SSBSECT.SSBSECT_CRSE_NUMB like ? "
					+ "OR SSBSECT.SSBSECT_CRSE_NUMB like ?) "
					+ "group by SFRSTCR.SFRSTCR_PIDM ";

		}
		return sqlstmt;
	}

	private String getAllCourseAndGradesSQL(boolean isPreviousGPA,
			boolean isDegreeFlag) {

		String sqlstmt = "";
		if (!isPreviousGPA) {
			if (isDegreeFlag) {
				sqlstmt = "select SPRIDEN.SPRIDEN_ID, "
						+ "SFRSTCR.SFRSTCR_CREDIT_HR, SHRGRDE.SHRGRDE_QUALITY_POINTS, "
						+ "SGBSTDN.SGBSTDN_LEVL_CODE, SSBSECT.SSBSECT_SUBJ_CODE,  "
						+ "SSBSECT.SSBSECT_CRSE_NUMB, STVCLAS.STVCLAS_CODE, "
						+ "SHRGRDE.SHRGRDE_CODE "
						+ "from SATURN.SPRIDEN SPRIDEN, "
						+ "SATURN.SFRSTCR SFRSTCR,SATURN.SSBSECT SSBSECT,SATURN.SHRGRDE SHRGRDE, "
						+ "SATURN.STVCLAS STVCLAS, BANINST1.SOVCLAS SOVCLAS, SATURN.SGBSTDN SGBSTDN "
						+ "where ( SFRSTCR.SFRSTCR_TERM_CODE = SSBSECT.SSBSECT_TERM_CODE "
						+ "and SFRSTCR.SFRSTCR_CRN = SSBSECT.SSBSECT_CRN "
						+ "and SPRIDEN.SPRIDEN_PIDM = SFRSTCR.SFRSTCR_PIDM "
						+ "and SGBSTDN.SGBSTDN_PIDM = SFRSTCR.SFRSTCR_PIDM  "
						+ "and SOVCLAS.SOVCLAS_PIDM = SFRSTCR.SFRSTCR_PIDM "
						+ "and SOVCLAS.SOVCLAS_TERM_CODE = SFRSTCR.SFRSTCR_TERM_CODE "
						+ "and SGBSTDN.SGBSTDN_TERM_CODE_EFF = SFRSTCR.SFRSTCR_TERM_CODE  "
						+ "and SHRGRDE.SHRGRDE_CODE = SFRSTCR.SFRSTCR_GRDE_CODE "
						+ "and SHRGRDE.SHRGRDE_LEVL_CODE = SFRSTCR.SFRSTCR_LEVL_CODE ) "
						+ "and ( SFRSTCR.SFRSTCR_CREDIT_HR <> ?"
						+ "and SFRSTCR.SFRSTCR_LEVL_CODE = ? "
						+ "and SHRGRDE.SHRGRDE_GPA_IND = ? "
						+ "and SFRSTCR.SFRSTCR_TERM_CODE >= ? "
						+ "and (SSBSECT.SSBSECT_CRSE_NUMB like ? "
						+ "or SSBSECT.SSBSECT_CRSE_NUMB like ?) "
						+ "and ( SGBSTDN.SGBSTDN_PIDM, SGBSTDN.SGBSTDN_TERM_CODE_EFF )  = "
						+ "( select SGBSTDN1.SGBSTDN_PIDM, "
						+ "Max( SGBSTDN1.SGBSTDN_TERM_CODE_EFF ) AS Max_SGBSTDN_TERM_CODE_EFF "
						+ "from SATURN.SGBSTDN SGBSTDN1 "
						+ "where SGBSTDN1.SGBSTDN_PIDM = SGBSTDN.SGBSTDN_PIDM "
						+ "group by SGBSTDN1.SGBSTDN_PIDM ) ) ";

			} else if (!isDegreeFlag) {
				sqlstmt = "select AS_STUDENT_ENROLLMENT_SUMMARY.ID, "
						+ "SFRSTCR.SFRSTCR_CREDIT_HR, SHRGRDE.SHRGRDE_QUALITY_POINTS, "
						+ "AS_STUDENT_ENROLLMENT_SUMMARY.LEVL_CODE, SSBSECT.SSBSECT_SUBJ_CODE, "
						+ "SSBSECT.SSBSECT_CRSE_NUMB, AS_STUDENT_ENROLLMENT_SUMMARY.CLAS_CODE, "
						+ "SHRGRDE.SHRGRDE_CODE "
						+ "from BANINST1.AS_STUDENT_ENROLLMENT_SUMMARY AS_STUDENT_ENROLLMENT_SUMMARY, "
						+ "SATURN.SFRSTCR SFRSTCR,SATURN.SSBSECT SSBSECT,SATURN.SHRGRDE SHRGRDE, "
						+ "SATURN.STVCLAS STVCLAS "
						+ "where ( SFRSTCR.SFRSTCR_TERM_CODE = SSBSECT.SSBSECT_TERM_CODE "
						+ "and SFRSTCR.SFRSTCR_CRN = SSBSECT.SSBSECT_CRN "
						+ "and AS_STUDENT_ENROLLMENT_SUMMARY.TERM_CODE_KEY = SFRSTCR.SFRSTCR_TERM_CODE "
						+ "and AS_STUDENT_ENROLLMENT_SUMMARY.PIDM_KEY = SFRSTCR.SFRSTCR_PIDM "
						+ "and SHRGRDE.SHRGRDE_CODE = SFRSTCR.SFRSTCR_GRDE_CODE "
						+ "and SHRGRDE.SHRGRDE_LEVL_CODE = SFRSTCR.SFRSTCR_LEVL_CODE "
						+ "and AS_STUDENT_ENROLLMENT_SUMMARY.CLAS_CODE = STVCLAS.STVCLAS_CODE ) "
						+ "and ( SFRSTCR.SFRSTCR_CREDIT_HR <> ? "
						+ "and SFRSTCR.SFRSTCR_LEVL_CODE = ? "
						+ "and SHRGRDE.SHRGRDE_GPA_IND = ? "
						+ "and SFRSTCR.SFRSTCR_TERM_CODE >= ? ) ";

			}

		} else if (isPreviousGPA) {
			if (isDegreeFlag) {

				sqlstmt = "select SPRIDEN.SPRIDEN_ID, "
						+ "SFRSTCR.SFRSTCR_CREDIT_HR, SHRGRDE.SHRGRDE_QUALITY_POINTS, "
						+ "SGBSTDN.SGBSTDN_LEVL_CODE, SSBSECT.SSBSECT_SUBJ_CODE,  "
						+ "SSBSECT.SSBSECT_CRSE_NUMB, STVCLAS.STVCLAS_CODE, "
						+ "SHRGRDE.SHRGRDE_CODE "
						+ "from SATURN.SPRIDEN SPRIDEN, "
						+ "SATURN.SFRSTCR SFRSTCR,SATURN.SSBSECT SSBSECT,SATURN.SHRGRDE SHRGRDE, "
						+ "SATURN.STVCLAS STVCLAS, BANINST1.SOVCLAS SOVCLAS, SATURN.SGBSTDN SGBSTDN "
						+ "where ( SFRSTCR.SFRSTCR_TERM_CODE = SSBSECT.SSBSECT_TERM_CODE "
						+ "and SFRSTCR.SFRSTCR_CRN = SSBSECT.SSBSECT_CRN "
						+ "and SPRIDEN.SPRIDEN_PIDM = SFRSTCR.SFRSTCR_PIDM "
						+ "and SGBSTDN.SGBSTDN_PIDM = SFRSTCR.SFRSTCR_PIDM  "
						+ "and SOVCLAS.SOVCLAS_PIDM = SFRSTCR.SFRSTCR_PIDM "
						+ "and SOVCLAS.SOVCLAS_TERM_CODE = SFRSTCR.SFRSTCR_TERM_CODE "
						+ "and SGBSTDN.SGBSTDN_TERM_CODE_EFF = SFRSTCR.SFRSTCR_TERM_CODE  "
						+ "and SHRGRDE.SHRGRDE_CODE = SFRSTCR.SFRSTCR_GRDE_CODE "
						+ "and SHRGRDE.SHRGRDE_LEVL_CODE = SFRSTCR.SFRSTCR_LEVL_CODE ) "
						+ "and ( SFRSTCR.SFRSTCR_CREDIT_HR <> ?"
						+ "and SFRSTCR.SFRSTCR_LEVL_CODE = ? "
						+ "and SHRGRDE.SHRGRDE_GPA_IND = ? "
						+ "and SFRSTCR.SFRSTCR_TERM_CODE >= ? and SFRSTCR.SFRSTCR_TERM_CODE < ? "
						+ "and (SSBSECT.SSBSECT_CRSE_NUMB like ? "
						+ "or SSBSECT.SSBSECT_CRSE_NUMB like ?) "
						+ "and ( SGBSTDN.SGBSTDN_PIDM, SGBSTDN.SGBSTDN_TERM_CODE_EFF )  = "
						+ "( select SGBSTDN1.SGBSTDN_PIDM, "
						+ "Max( SGBSTDN1.SGBSTDN_TERM_CODE_EFF ) AS Max_SGBSTDN_TERM_CODE_EFF "
						+ "from SATURN.SGBSTDN SGBSTDN1 "
						+ "where SGBSTDN1.SGBSTDN_PIDM = SGBSTDN.SGBSTDN_PIDM "
						+ "group by SGBSTDN1.SGBSTDN_PIDM ) ) ";

			}
			if (!isDegreeFlag) {
				sqlstmt = "select AS_STUDENT_ENROLLMENT_SUMMARY.ID, "
						+ "SFRSTCR.SFRSTCR_CREDIT_HR, SHRGRDE.SHRGRDE_QUALITY_POINTS, "
						+ "AS_STUDENT_ENROLLMENT_SUMMARY.LEVL_CODE, SSBSECT.SSBSECT_SUBJ_CODE, "
						+ "SSBSECT.SSBSECT_CRSE_NUMB, AS_STUDENT_ENROLLMENT_SUMMARY.CLAS_CODE, "
						+ "SHRGRDE.SHRGRDE_CODE "
						+ "from BANINST1.AS_STUDENT_ENROLLMENT_SUMMARY AS_STUDENT_ENROLLMENT_SUMMARY, "
						+ "SATURN.SFRSTCR SFRSTCR,SATURN.SSBSECT SSBSECT,SATURN.SHRGRDE SHRGRDE, "
						+ "SATURN.STVCLAS STVCLAS "
						+ "where ( SFRSTCR.SFRSTCR_TERM_CODE = SSBSECT.SSBSECT_TERM_CODE "
						+ "and SFRSTCR.SFRSTCR_CRN = SSBSECT.SSBSECT_CRN "
						+ "and AS_STUDENT_ENROLLMENT_SUMMARY.TERM_CODE_KEY = SFRSTCR.SFRSTCR_TERM_CODE "
						+ "and AS_STUDENT_ENROLLMENT_SUMMARY.PIDM_KEY = SFRSTCR.SFRSTCR_PIDM "
						+ "and SHRGRDE.SHRGRDE_CODE = SFRSTCR.SFRSTCR_GRDE_CODE "
						+ "and SHRGRDE.SHRGRDE_LEVL_CODE = SFRSTCR.SFRSTCR_LEVL_CODE "
						+ "and AS_STUDENT_ENROLLMENT_SUMMARY.CLAS_CODE = STVCLAS.STVCLAS_CODE ) "
						+ "and ( SFRSTCR.SFRSTCR_CREDIT_HR <> ? "
						+ "and SFRSTCR.SFRSTCR_LEVL_CODE = ? "
						+ "and SHRGRDE.SHRGRDE_GPA_IND = ? "
						+ "and SFRSTCR.SFRSTCR_TERM_CODE >= ? and SFRSTCR.SFRSTCR_TERM_CODE < ? ) ";
			}

		}
		return sqlstmt;

	}
	private String getStudentProgramSQL(){
		
		
		String sqlstmt = "select SPRIDEN.SPRIDEN_ID  as ID , " +
	       "SPRIDEN.SPRIDEN_LAST_NAME as LastName, " +
	       "SPRIDEN.SPRIDEN_FIRST_NAME as FirstName, " +
	       "STVCOLL.STVCOLL_DESC as Faculty, " +
	       "STVMAJR.STVMAJR_DESC  as Major, " +
	       "SMRPRLE.SMRPRLE_PROGRAM_DESC as Program " +
	  "from SATURN.SPRIDEN SPRIDEN, " +
	       "SATURN.SGBSTDN SGBSTDN, " +
	       "SATURN.STVCOLL STVCOLL, " +
	       "SATURN.STVMAJR STVMAJR, " +
	       "SATURN.SMRPRLE SMRPRLE " +
	 "where ( SPRIDEN.SPRIDEN_PIDM = SGBSTDN.SGBSTDN_PIDM "+
	         "and STVCOLL.STVCOLL_CODE = SGBSTDN.SGBSTDN_COLL_CODE_1 " +
	         "and SGBSTDN.SGBSTDN_MAJR_CODE_1 = STVMAJR.STVMAJR_CODE "+
	         "and SGBSTDN.SGBSTDN_PROGRAM_1 = SMRPRLE.SMRPRLE_PROGRAM ) " +
	   "and ( SPRIDEN.SPRIDEN_CHANGE_IND IS NULL "+
	         "and SGBSTDN.SGBSTDN_STYP_CODE in  (?,?,?,?,?,?,?,?,?,?) " +
	         "and SGBSTDN.SGBSTDN_STST_CODE in (?,?,?,?) "+
	         "and SPRIDEN.SPRIDEN_ID = ? " +
	         "and ( SGBSTDN.SGBSTDN_PIDM, SGBSTDN.SGBSTDN_TERM_CODE_EFF )  = " +
	         "( select SGBSTDN1.SGBSTDN_PIDM, " +
	                  "Max( SGBSTDN1.SGBSTDN_TERM_CODE_EFF )  as Max_SGBSTDN_TERM_CODE_EFF " +
	             "from SATURN.SGBSTDN SGBSTDN1 " +
	            "where SGBSTDN1.SGBSTDN_PIDM = SGBSTDN.SGBSTDN_PIDM " +
	            "group by SGBSTDN1.SGBSTDN_PIDM ) ) " ;

		return sqlstmt;
	}

	private String getCourseAndGradesSQL(boolean isPreviousGPA,
			boolean isDegreeFlag) {

		String sqlstmt = "";
		if (!isPreviousGPA) {
			if (isDegreeFlag) {

				sqlstmt = "select distinct SFRSTCR_TERM_CODE, SPRIDEN.SPRIDEN_ID, "
						+ "SFRSTCR.SFRSTCR_CREDIT_HR, SHRGRDE.SHRGRDE_QUALITY_POINTS, "
						+ "SGBSTDN.SGBSTDN_LEVL_CODE, SSBSECT.SSBSECT_SUBJ_CODE, "
						+ "SSBSECT.SSBSECT_CRSE_NUMB, SSBSECT_CRN, "
						+ "SHRGRDE.SHRGRDE_CODE, SFRSTCR_PIDM "
						+ "FROM SATURN.SFRSTCR, SATURN.SPRIDEN, SATURN.SSBSECT, "
						+ "SHRGRDE, BANINST1.SOVCLAS SOVCLAS, SATURN.STVCLAS, SATURN.SGBSTDN "
						+ "WHERE SPRIDEN_PIDM = SFRSTCR_PIDM "
						+ "AND SHRGRDE.SHRGRDE_LEVL_CODE = SFRSTCR.SFRSTCR_LEVL_CODE "
						+ "AND SOVCLAS.SOVCLAS_TERM_CODE =  SFRSTCR_TERM_CODE "
						+ "AND SOVCLAS.SOVCLAS_PIDM = SFRSTCR_PIDM "
						+ "AND SSBSECT_CRN = SFRSTCR_CRN "
						+ "AND SOVCLAS.SOVCLAS_CLAS_CODE = STVCLAS_CODE "
						+ "AND SHRGRDE.SHRGRDE_CODE = SFRSTCR.SFRSTCR_GRDE_CODE "
						+ "AND SGBSTDN_TERM_CODE_EFF = SFRSTCR_TERM_CODE "
						+ "AND SSBSECT_TERM_CODE = SFRSTCR_TERM_CODE "
						+ "AND SHRGRDE.SHRGRDE_LEVL_CODE = SFRSTCR.SFRSTCR_LEVL_CODE "
						+ "AND SGBSTDN_PIDM = SFRSTCR_PIDM "
						+ "AND SFRSTCR.SFRSTCR_CREDIT_HR <> ? "
						+ "AND SFRSTCR.SFRSTCR_LEVL_CODE = ? "
						+ "AND SHRGRDE.SHRGRDE_GPA_IND = ? "
						+ "AND SFRSTCR_TERM_CODE >= ?  "
						+ "AND SPRIDEN_ID = ? "
						+ "and (SSBSECT.SSBSECT_CRSE_NUMB like ? "
						+ "OR SSBSECT.SSBSECT_CRSE_NUMB like ?) "
						+ "AND "
						+ " ORDER BY concat(SSBSECT.SSBSECT_SUBJ_CODE,SSBSECT.SSBSECT_CRSE_NUMB)";
				
				sqlstmt = "select distinct SHRTCKN.SHRTCKN_TERM_CODE,SPRIDEN.SPRIDEN_ID, "
						+ "SHRTCKG.SHRTCKG_CREDIT_HOURS,SHRGRDE.SHRGRDE_QUALITY_POINTS, "
						+ "SHRTCKL.SHRTCKL_LEVL_CODE,SHRTCKN.SHRTCKN_SUBJ_CODE,SHRTCKN.SHRTCKN_CRSE_NUMB, "
						+ "SHRTCKG.SHRTCKG_GRDE_CODE_FINAL,SHRTCKN.SHRTCKN_CRN,SHRTCKN_PIDM "
						+ "from SATURN.SHRTCKN SHRTCKN, "
						+ "SATURN.SHRTCKL SHRTCKL, "
						+ "SATURN.SHRTCKG SHRTCKG, "
						+ "SATURN.SHRGRDE SHRGRDE, "
						+ "SATURN.SPRIDEN SPRIDEN "
						+ "where ( SHRTCKN.SHRTCKN_PIDM = SHRTCKG.SHRTCKG_PIDM "
						+ "and SHRTCKN.SHRTCKN_TERM_CODE = SHRTCKG.SHRTCKG_TERM_CODE "
						+ "and SHRTCKN.SHRTCKN_SEQ_NO = SHRTCKG.SHRTCKG_TCKN_SEQ_NO "
						+ "and SHRTCKN.SHRTCKN_PIDM = SHRTCKL.SHRTCKL_PIDM "
						+ "and SHRTCKN.SHRTCKN_TERM_CODE = SHRTCKL.SHRTCKL_TERM_CODE "
						+ "and SHRTCKN.SHRTCKN_SEQ_NO = SHRTCKL.SHRTCKL_TCKN_SEQ_NO "
						+ "and SHRGRDE.SHRGRDE_CODE = SHRTCKG.SHRTCKG_GRDE_CODE_FINAL "
						+ "and SHRGRDE.SHRGRDE_LEVL_CODE = SHRTCKL.SHRTCKL_LEVL_CODE "
						+ "and SPRIDEN.SPRIDEN_PIDM = SHRTCKN.SHRTCKN_PIDM ) "
						+ "and ( SHRTCKG.SHRTCKG_CREDIT_HOURS <> ? "
						+ " and SHRTCKL.SHRTCKL_LEVL_CODE = ? "
						+ "and SHRGRDE.SHRGRDE_GPA_IND = ? "
						+ "and SHRTCKN.SHRTCKN_TERM_CODE >= ?  "
						+ "and SPRIDEN.SPRIDEN_ID = ? "
						+ "and (SHRTCKN.SHRTCKN_CRSE_NUMB like ? or SHRTCKN.SHRTCKN_CRSE_NUMB like ?) "
						+ "and SHRTCKG.SHRTCKG_GMOD_CODE = ? "
						+ "and SPRIDEN.SPRIDEN_CHANGE_IND is null "
						+ "and ( SHRTCKG.SHRTCKG_PIDM, SHRTCKG.SHRTCKG_TERM_CODE, SHRTCKG.SHRTCKG_TCKN_SEQ_NO, SHRTCKG.SHRTCKG_SEQ_NO )  = "
						+ "( select SHRTCKG1.SHRTCKG_PIDM, "
						+ "SHRTCKG1.SHRTCKG_TERM_CODE, "
						+ "SHRTCKG1.SHRTCKG_TCKN_SEQ_NO, "
						+ "Max( SHRTCKG1.SHRTCKG_SEQ_NO ) as Max_SHRTCKG_SEQ_NO "
						+ "from SATURN.SHRTCKG SHRTCKG1 "
						+ "where SHRTCKG1.SHRTCKG_PIDM = SHRTCKG.SHRTCKG_PIDM "
						+ "and SHRTCKG1.SHRTCKG_TERM_CODE = SHRTCKG.SHRTCKG_TERM_CODE "
						+ "and SHRTCKG1.SHRTCKG_TCKN_SEQ_NO = SHRTCKG.SHRTCKG_TCKN_SEQ_NO "
						+ "group by SHRTCKG1.SHRTCKG_PIDM, "
						+ "SHRTCKG1.SHRTCKG_TERM_CODE,"
						+ "SHRTCKG1.SHRTCKG_TCKN_SEQ_NO ) )";


			}
		} else if (isPreviousGPA) {
			if (isDegreeFlag) {

				sqlstmt = "select distinct SFRSTCR_TERM_CODE, SPRIDEN.SPRIDEN_ID, "
						+ "SFRSTCR.SFRSTCR_CREDIT_HR, SHRGRDE.SHRGRDE_QUALITY_POINTS, "
						+ "SGBSTDN.SGBSTDN_LEVL_CODE, SSBSECT.SSBSECT_SUBJ_CODE, "
						+ "SSBSECT.SSBSECT_CRSE_NUMB, STVCLAS.STVCLAS_CODE, "
						+ "SHRGRDE.SHRGRDE_CODE, SFRSTCR_CRN, SFRSTCR_PIDM "
						+ "FROM SATURN.SFRSTCR, SATURN.SPRIDEN, SATURN.SSBSECT, "
						+ "SHRGRDE, BANINST1.SOVCLAS SOVCLAS, SATURN.STVCLAS, SATURN.SGBSTDN "
						+ "WHERE SPRIDEN_PIDM = SFRSTCR_PIDM "
						+ "AND SHRGRDE.SHRGRDE_LEVL_CODE = SFRSTCR.SFRSTCR_LEVL_CODE "
						+ "AND SOVCLAS.SOVCLAS_TERM_CODE =  SFRSTCR_TERM_CODE "
						+ "AND SOVCLAS.SOVCLAS_PIDM = SFRSTCR_PIDM "
						+ "AND SSBSECT_CRN = SFRSTCR_CRN "
						+ "AND SOVCLAS.SOVCLAS_CLAS_CODE = STVCLAS_CODE "
						+ "AND SHRGRDE.SHRGRDE_CODE = SFRSTCR.SFRSTCR_GRDE_CODE "
						+ "AND SGBSTDN_TERM_CODE_EFF = SFRSTCR_TERM_CODE "
						+ "AND SSBSECT_TERM_CODE = SFRSTCR_TERM_CODE "
						+ "AND SHRGRDE.SHRGRDE_LEVL_CODE = SFRSTCR.SFRSTCR_LEVL_CODE "
						+ "AND SGBSTDN_PIDM = SFRSTCR_PIDM "
						+ "AND SFRSTCR.SFRSTCR_CREDIT_HR <> ? "
						+ "AND SFRSTCR.SFRSTCR_LEVL_CODE = ? "
						+ "AND SHRGRDE.SHRGRDE_GPA_IND = ? "
						+ "AND (SFRSTCR_TERM_CODE >= ? AND SFRSTCR_TERM_CODE < ?) "
						+ "AND SPRIDEN_ID = ? "
						+ "and (SSBSECT.SSBSECT_CRSE_NUMB like ? "
						+ "OR SSBSECT.SSBSECT_CRSE_NUMB like ?) ";

				sqlstmt = "select distinct SHRTCKN.SHRTCKN_TERM_CODE,SPRIDEN.SPRIDEN_ID, "
						+ "SHRTCKG.SHRTCKG_CREDIT_HOURS,SHRGRDE.SHRGRDE_QUALITY_POINTS, "
						+ "SHRTCKL.SHRTCKL_LEVL_CODE,SHRTCKN.SHRTCKN_SUBJ_CODE,SHRTCKN.SHRTCKN_CRSE_NUMB, "
						+ "SHRTCKG.SHRTCKG_GRDE_CODE_FINAL,SHRTCKN.SHRTCKN_CRN,SHRTCKN_PIDM "
						+ "from SATURN.SHRTCKN SHRTCKN, "
						+ "SATURN.SHRTCKL SHRTCKL, "
						+ "SATURN.SHRTCKG SHRTCKG, "
						+ "SATURN.SHRGRDE SHRGRDE, "
						+ "SATURN.SPRIDEN SPRIDEN "
						+ "where ( SHRTCKN.SHRTCKN_PIDM = SHRTCKG.SHRTCKG_PIDM "
						+ "and SHRTCKN.SHRTCKN_TERM_CODE = SHRTCKG.SHRTCKG_TERM_CODE "
						+ "and SHRTCKN.SHRTCKN_SEQ_NO = SHRTCKG.SHRTCKG_TCKN_SEQ_NO "
						+ "and SHRTCKN.SHRTCKN_PIDM = SHRTCKL.SHRTCKL_PIDM "
						+ "and SHRTCKN.SHRTCKN_TERM_CODE = SHRTCKL.SHRTCKL_TERM_CODE "
						+ "and SHRTCKN.SHRTCKN_SEQ_NO = SHRTCKL.SHRTCKL_TCKN_SEQ_NO "
						+ "and SHRGRDE.SHRGRDE_CODE = SHRTCKG.SHRTCKG_GRDE_CODE_FINAL "
						+ "and SHRGRDE.SHRGRDE_LEVL_CODE = SHRTCKL.SHRTCKL_LEVL_CODE "
						+ "and SPRIDEN.SPRIDEN_PIDM = SHRTCKN.SHRTCKN_PIDM ) "
						+ "and ( SHRTCKG.SHRTCKG_CREDIT_HOURS <> ? "
						+ " and SHRTCKL.SHRTCKL_LEVL_CODE = ? "
						+ "and SHRGRDE.SHRGRDE_GPA_IND = ? "
						+ "and (SHRTCKN.SHRTCKN_TERM_CODE >= ? and SHRTCKN.SHRTCKN_TERM_CODE < ?) "
						+ "and SPRIDEN.SPRIDEN_ID = ? "
						+ "and (SHRTCKN.SHRTCKN_CRSE_NUMB like ? or SHRTCKN.SHRTCKN_CRSE_NUMB like ?) "
						+ "and SHRTCKG.SHRTCKG_GMOD_CODE = ? "
						+ "and SPRIDEN.SPRIDEN_CHANGE_IND is null "
						+ "and ( SHRTCKG.SHRTCKG_PIDM, SHRTCKG.SHRTCKG_TERM_CODE, SHRTCKG.SHRTCKG_TCKN_SEQ_NO, SHRTCKG.SHRTCKG_SEQ_NO )  = "
						+ "( select SHRTCKG1.SHRTCKG_PIDM, "
						+ "SHRTCKG1.SHRTCKG_TERM_CODE, "
						+ "SHRTCKG1.SHRTCKG_TCKN_SEQ_NO, "
						+ "Max( SHRTCKG1.SHRTCKG_SEQ_NO ) as Max_SHRTCKG_SEQ_NO "
						+ "from SATURN.SHRTCKG SHRTCKG1 "
						+ "where SHRTCKG1.SHRTCKG_PIDM = SHRTCKG.SHRTCKG_PIDM "
						+ "and SHRTCKG1.SHRTCKG_TERM_CODE = SHRTCKG.SHRTCKG_TERM_CODE "
						+ "and SHRTCKG1.SHRTCKG_TCKN_SEQ_NO = SHRTCKG.SHRTCKG_TCKN_SEQ_NO "
						+ "group by SHRTCKG1.SHRTCKG_PIDM, "
						+ "SHRTCKG1.SHRTCKG_TERM_CODE,"
						+ "SHRTCKG1.SHRTCKG_TCKN_SEQ_NO ) )";

			}

		}
		return sqlstmt;

	}

	private String getCourseAndGradesOtherInstitutionsSQL(
			boolean isPreviousGPA, boolean isDegreeFlag) {
		String sqlstmt = "";

		if (isPreviousGPA) {
			if (isDegreeFlag) {

				sqlstmt = "select distinct SHRTRCE.SHRTRCE_TERM_CODE_EFF,SPRIDEN.SPRIDEN_ID,SHRTRCE.SHRTRCE_CREDIT_HOURS, "
						+ "SHRGRDE.SHRGRDE_QUALITY_POINTS,SHRTRCE.SHRTRCE_LEVL_CODE,SHRTRCE.SHRTRCE_SUBJ_CODE,SHRTRCE.SHRTRCE_CRSE_NUMB, "
						+ "SHRGRDE.SHRGRDE_CODE, 'CRN' as CRN, SHRTRCE.SHRTRCE_PIDM "
						+ "from SATURN.SHRTRCE SHRTRCE,SATURN.SHRGRDE SHRGRDE,SATURN.SPRIDEN SPRIDEN, SATURN.SHRTCKL SHRTCKL "
						+ "where ( SHRTRCE.SHRTRCE_PIDM = SPRIDEN.SPRIDEN_PIDM "
						+ "and SHRTRCE.SHRTRCE_LEVL_CODE = SHRGRDE.SHRGRDE_LEVL_CODE "
						+ "and SHRTRCE.SHRTRCE_GRDE_CODE = SHRGRDE.SHRGRDE_CODE ) "
						+ "and ( SHRTRCE.SHRTRCE_CREDIT_HOURS <> ? "
						+ "and SHRTRCE.SHRTRCE_LEVL_CODE = ? "
						+ "and SHRGRDE.SHRGRDE_GPA_IND = ? "
						+ "and (SHRTRCE.SHRTRCE_TERM_CODE_EFF >= ? and SHRTRCE.SHRTRCE_TERM_CODE_EFF < ?) "
						+ "and SPRIDEN.SPRIDEN_ID =  ? "
						+ "and (SHRTRCE.SHRTRCE_CRSE_NUMB like ? or SHRTRCE.SHRTRCE_CRSE_NUMB like ? ) "
						+ "and SHRTRCE.SHRTRCE_GMOD_CODE = ? "
						+ " and SPRIDEN.SPRIDEN_CHANGE_IND is null)";

			}
		} else if (!isPreviousGPA) {
			if (isDegreeFlag) {
				sqlstmt = "select distinct SHRTRCE.SHRTRCE_TERM_CODE_EFF,SPRIDEN.SPRIDEN_ID,SHRTRCE.SHRTRCE_CREDIT_HOURS, "
						+ "SHRGRDE.SHRGRDE_QUALITY_POINTS,SHRTRCE.SHRTRCE_LEVL_CODE,SHRTRCE.SHRTRCE_SUBJ_CODE,SHRTRCE.SHRTRCE_CRSE_NUMB, "
						+ "SHRGRDE.SHRGRDE_CODE, 'CRN' as CRN, SHRTRCE.SHRTRCE_PIDM "
						+ "from SATURN.SHRTRCE SHRTRCE,SATURN.SHRGRDE SHRGRDE,SATURN.SPRIDEN SPRIDEN "
						+ "where ( SHRTRCE.SHRTRCE_PIDM = SPRIDEN.SPRIDEN_PIDM "
						+ "and SHRTRCE.SHRTRCE_LEVL_CODE = SHRGRDE.SHRGRDE_LEVL_CODE "
						+ "and SHRTRCE.SHRTRCE_GRDE_CODE = SHRGRDE.SHRGRDE_CODE ) "
						+ "and ( SHRTRCE.SHRTRCE_CREDIT_HOURS <> ? "
						+ "and SHRTRCE.SHRTRCE_LEVL_CODE = ? "
						+ "and SHRGRDE.SHRGRDE_GPA_IND = ? "
						+ "and SHRTRCE.SHRTRCE_TERM_CODE_EFF >= ?  "
						+ "and SPRIDEN.SPRIDEN_ID =  ? "
						+ "and (SHRTRCE.SHRTRCE_CRSE_NUMB like ? or SHRTRCE.SHRTRCE_CRSE_NUMB like ? ) "
						+ " and SPRIDEN.SPRIDEN_CHANGE_IND is null)";
			}
		}
		return sqlstmt;

	}

	private String getDegreeAwardedSQL() {

		String sqlstmt = "";

		sqlstmt = "select SPRIDEN.SPRIDEN_ID,SHRDGMR.SHRDGMR_DEGS_CODE,STVHONR.STVHONR_DESC, "
				+ "SHRDGMR.SHRDGMR_SEQ_NO,SHRDGMR.SHRDGMR_ACYR_CODE "
				+ "from SATURN.SHRDGMR SHRDGMR,SATURN.SHRDGIH SHRDGIH,SATURN.STVHONR STVHONR, "
				+ "SATURN.SPRIDEN SPRIDEN,SATURN.SGBSTDN SGBSTDN "
				+ "where ( SHRDGMR.SHRDGMR_PIDM = SHRDGIH.SHRDGIH_PIDM "
				+ "and SHRDGMR.SHRDGMR_SEQ_NO = SHRDGIH.SHRDGIH_DGMR_SEQ_NO "
				+ "and SHRDGIH.SHRDGIH_HONR_CODE = STVHONR.STVHONR_CODE "
				+ "and SPRIDEN.SPRIDEN_PIDM = SHRDGMR.SHRDGMR_PIDM "
				+ "and SGBSTDN.SGBSTDN_PIDM = SPRIDEN.SPRIDEN_PIDM ) "
				+ "and ( SPRIDEN.SPRIDEN_ID = ? "
				+ "and ( SGBSTDN.SGBSTDN_PIDM, SGBSTDN.SGBSTDN_TERM_CODE_EFF )  = "
				+ "( select SGBSTDN1.SGBSTDN_PIDM, "
				+ "Max( SGBSTDN1.SGBSTDN_TERM_CODE_EFF )  as Max_SGBSTDN_TERM_CODE_EFF "
				+ "from SATURN.SGBSTDN SGBSTDN1 "
				+ "where SGBSTDN1.SGBSTDN_PIDM = SGBSTDN.SGBSTDN_PIDM "
				+ "group by SGBSTDN1.SGBSTDN_PIDM ) ) ";

		return sqlstmt;
	}

	private String getUpdatedMarksSQL() {

		String sqlstmt = "select SHRTCKG.SHRTCKG_GRDE_CODE_FINAL,SHRTCKN.SHRTCKN_PIDM, "
				+ "SHRTCKN.SHRTCKN_TERM_CODE,SHRTCKN.SHRTCKN_CRN,"
				+ "SHRTCKG.SHRTCKG_SEQ_NO "
				+ "from SATURN.SHRTCKG SHRTCKG, "
				+ "SATURN.SHRTCKN SHRTCKN "
				+ "where ( SHRTCKG.SHRTCKG_TERM_CODE = SHRTCKN.SHRTCKN_TERM_CODE (+) "
				+ "and SHRTCKG.SHRTCKG_TCKN_SEQ_NO = SHRTCKN.SHRTCKN_SEQ_NO (+) "
				+ "and SHRTCKN.SHRTCKN_PIDM = SHRTCKG.SHRTCKG_PIDM ) "
				+ "and ( SHRTCKN.SHRTCKN_CRN = ? "
				+ "and SHRTCKG.SHRTCKG_PIDM = ? "
				+ "and SHRTCKN.SHRTCKN_TERM_CODE = ? "
				+ "and ( SHRTCKG.SHRTCKG_PIDM, SHRTCKG.SHRTCKG_TERM_CODE, SHRTCKG.SHRTCKG_TCKN_SEQ_NO, SHRTCKG.SHRTCKG_SEQ_NO )  = "
				+ "( select SHRTCKG1.SHRTCKG_PIDM, "
				+ "SHRTCKG1.SHRTCKG_TERM_CODE, "
				+ "SHRTCKG1.SHRTCKG_TCKN_SEQ_NO, "
				+ "Max( SHRTCKG1.SHRTCKG_SEQ_NO )  as Max_SHRTCKG_SEQ_NO "
				+ "from SATURN.SHRTCKG SHRTCKG1 "
				+ "where SHRTCKG1.SHRTCKG_PIDM = SHRTCKN.SHRTCKN_PIDM "
				+ "and SHRTCKG1.SHRTCKG_TERM_CODE = SHRTCKG.SHRTCKG_TERM_CODE "
				+ "and SHRTCKG1.SHRTCKG_TCKN_SEQ_NO = SHRTCKG.SHRTCKG_TCKN_SEQ_NO "
				+ "group by SHRTCKG1.SHRTCKG_PIDM, "
				+ "SHRTCKG1.SHRTCKG_TERM_CODE, "
				+ "SHRTCKG1.SHRTCKG_TCKN_SEQ_NO ) ) ";

		return sqlstmt;
	}
	
	public StudentProgram getStudentProgram(String id){
		
		try {
			  
	         	PreparedStatement prepStmt = conn
						.prepareStatement(getStudentProgramSQL());
				
				prepStmt.setString(1, "N");
				prepStmt.setString(2, "T");
				prepStmt.setString(3, "R");
				prepStmt.setString(4, "F");
				prepStmt.setString(5, "S");
				prepStmt.setString(6, "C");
				prepStmt.setString(7, "E");
				prepStmt.setString(8, "V");
				prepStmt.setString(9, "O");
				prepStmt.setString(10, "G");
				
				prepStmt.setString(11, "EX");
				prepStmt.setString(12, "IG");
				prepStmt.setString(13,"CA");
				prepStmt.setString(14,"AS");
				
				prepStmt.setString(15, id);
				
				ResultSet rs = prepStmt.executeQuery();
				
				StudentProgram studentProgram = new StudentProgram();
				
				while (rs.next()){
					studentProgram.setId(id);
					studentProgram.setLastname(rs.getString(2));
					studentProgram.setFirstname(rs.getString(3));
					studentProgram.setFaculty(rs.getString(4));
					studentProgram.setMajor(rs.getString(5));
					studentProgram.setProgram(rs.getString(6));
				}
			
				return studentProgram;
				
		} catch (SQLException e){
			e.printStackTrace();
		}
		
		return null;
	}

	public DegreeAwarded getAwarded(String id) {

		try {
			PreparedStatement prepStmt = conn
					.prepareStatement(getDegreeAwardedSQL());
			prepStmt.setString(1, id);

			ResultSet rs = prepStmt.executeQuery();

			DegreeAwarded da = new DegreeAwarded();
			if (rs.next()) {
				da.setAward(rs.getString(2));
				da.setDegree(rs.getString(3));
			} else {
				da.setAward("no award");
				da.setDegree("no degree");
			}

			return da;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public double getGPA(String term, String id, boolean isGPAFlag,
			boolean isDegree, boolean isPreviousGPA) {

		String selectStatement = "";

		if (isDegree) {
			selectStatement = getGPASQL(isPreviousGPA);
		} else {
			selectStatement = getAllAccumGPASQL(isPreviousGPA);
		}

		try {

			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);
			prepStmt.setInt(1, 0);
			prepStmt.setString(2, "UG");
			prepStmt.setString(3, "Y");
			prepStmt.setString(4, term);

			if (isPreviousGPA)
				prepStmt.setString(5, "201410");

			if (!isPreviousGPA)
				prepStmt.setString(5, id);
			else
				prepStmt.setString(6, id);

			if (isDegree) {

				if (isPreviousGPA) {
					prepStmt.setString(7, "2%");
					prepStmt.setString(8, "3%");
				} else {
					prepStmt.setString(6, "2%");
					prepStmt.setString(7, "3%");
				}
			}

			ResultSet rs = prepStmt.executeQuery();

			if (rs.next()) {
				if (isGPAFlag)
					return rs.getDouble(4);
				else if (!isGPAFlag)
					return rs.getDouble(3);

			}

		} catch (Exception ex) {

		}
		return 0;
	}

	public void insertOldGPA(String term, boolean isPreviousGPA) {

		String selectStatement = getAllAccumGPASQL(isPreviousGPA);

		// String selectStatement = getAllOldDegreeGPASQL();

		try {

			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);
			prepStmt.setInt(1, 0);
			prepStmt.setString(2, "UG");
			prepStmt.setString(3, "Y");
			prepStmt.setString(4, term);

			/*
			 * prepStmt.setString(5, "2%"); prepStmt.setString(6, "3%");
			 */
			ResultSet rs = prepStmt.executeQuery();

			StoredGPADb store = new StoredGPADb("admin", "kentish",
					"systemsman04");

			GPARemoteService remote = new GPARemoteService();

			while (rs.next()) {

				double convertedGPA = remote.convertedGPA(rs.getDouble(4));

				store.insertGPA(rs.getString(1), rs.getDouble(3), convertedGPA,
						rs.getDouble(2), "D");
			}
			store.closeConnections();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public ArrayList gatAllCourseAndGrades(String term, boolean isPreviousGPA,
			boolean isDegreeFlag) {

		ArrayList list = new ArrayList();

		String selectStatement = getAllCourseAndGradesSQL(isPreviousGPA,
				isDegreeFlag);

		try {

			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);
			prepStmt.setInt(1, 0);
			prepStmt.setString(2, "UG");
			prepStmt.setString(3, "Y");
			prepStmt.setString(4, term);
			if (isPreviousGPA) {

				prepStmt.setString(5, "201410");
				if (isDegreeFlag) {
					prepStmt.setString(6, "2%");
					prepStmt.setString(7, "3%");
				}
			} else if (!isPreviousGPA) {
				if (isDegreeFlag) {
					prepStmt.setString(6, "2%");
					prepStmt.setString(7, "3%");
				}
			}

			ResultSet rs = prepStmt.executeQuery();

			while (rs.next()) {

				CourseAndGrade cag = new CourseAndGrade();
				cag.setId(rs.getString(1));
				cag.setClas(rs.getString(7));
				cag.setCourseNumb(rs.getString(6));
				cag.setCreditHr(rs.getDouble(2));
				cag.setGrade(rs.getString(8));
				cag.setQualityPoints(rs.getDouble(3));
				cag.setSubjCode(rs.getString(5));
				MessageLogger.out.println(cag.getId() + " " + cag.getSubjCode()
						+ " " + cag.getCourseNumb());
				list.add(cag);
			}
			int size = list.size();
			System.out.println(size);
			return list;

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;

	}

	public ArrayList getCourseAndGrades(String term, String id,
			boolean isPreviousGPA, boolean isDegreeFlag) {

		ArrayList list = new ArrayList();

		String selectStatement = getCourseAndGradesSQL(isPreviousGPA,
				isDegreeFlag);

		try {

			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);
			prepStmt.setInt(1, 0);
			prepStmt.setString(2, "UG");
			prepStmt.setString(3, "Y");
			prepStmt.setString(4, term);
			if (isPreviousGPA) {

				prepStmt.setString(5, "201410");
				prepStmt.setString(6, id);
				if (isDegreeFlag) {
					prepStmt.setString(7, "2%");
					prepStmt.setString(8, "3%");
					prepStmt.setString(9, "S");
				}
			} else if (!isPreviousGPA) {
				prepStmt.setString(5, id);
				if (isDegreeFlag) {
					prepStmt.setString(6, "2%");
					prepStmt.setString(7, "3%");
					prepStmt.setString(8, "S");
				}
			}

			ResultSet rs = prepStmt.executeQuery();

			while (rs.next()) {

				
				CourseAndGrade cag = new CourseAndGrade();
				cag.setClas(" ");
				cag.setCourseNumb(rs.getString(7));
				cag.setCreditHr(rs.getDouble(3));
				cag.setGrade(rs.getString(8));

				String newMark = getUpdatedMarks(rs.getInt(10),
						rs.getString(1), rs.getString(9));
				if (newMark != null) {
					cag.setGrade(newMark);
				}
				cag.setQualityPoints(rs.getDouble(4));
				cag.setSubjCode(rs.getString(6));

				System.out.println(cag.getSubjCode() + cag.getCourseNumb());

				list.add(cag);
			}

			System.out.println(term + ", " + id + ", "+ isPreviousGPA + ", " +isDegreeFlag);
			getCouseAndGradesOtherInstitutions(term, id, isPreviousGPA,
					isDegreeFlag, list);

			int size = list.size();
			System.out.println(size);
			return list;

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;

	}

	private ArrayList getCouseAndGradesOtherInstitutions(String term,
			String id, boolean isPreviousGPA, boolean isDegreeFlag,
			ArrayList list) {
		ResultSet rs = null;
		PreparedStatement prepStmt = null;
		String selectStatement = getCourseAndGradesOtherInstitutionsSQL(
				isPreviousGPA, isDegreeFlag);
		try {

			prepStmt = conn.prepareStatement(selectStatement);
			prepStmt.setInt(1, 0);
			prepStmt.setString(2, "UG");
			prepStmt.setString(3, "Y");
			prepStmt.setString(4, term);
			if (isPreviousGPA) {

				prepStmt.setString(5, "201510");
				prepStmt.setString(6, id);
				if (isDegreeFlag) {
					prepStmt.setString(7, "2%");
					prepStmt.setString(8, "3%");
					prepStmt.setString(9, "S");
				}
			} else if (!isPreviousGPA) {
				prepStmt.setString(5, id);
				if (isDegreeFlag) {
					prepStmt.setString(6, "2%");
					prepStmt.setString(7, "3%");
					//prepStmt.setString(8, "S");
				}
			}

			rs = prepStmt.executeQuery();

			while (rs.next()) {

				CourseAndGrade cag = new CourseAndGrade();
				cag.setClas(" ");
				cag.setCourseNumb(rs.getString(7));
				cag.setCreditHr(rs.getDouble(3));
				cag.setGrade(rs.getString(8));
				cag.setQualityPoints(rs.getDouble(4));
				cag.setSubjCode(rs.getString(6));
				System.out.println(cag.getSubjCode() + cag.getCourseNumb());
				list.add(cag);
			}
			rs.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		} 
		try {
			rs.close();
			prepStmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return list;

	}

	private String getUpdatedMarks(long pidm, String termCode, String crn) {

		String selectStatement = getUpdatedMarksSQL();

		String grade = null;
		try {

			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);
			prepStmt.setString(1, crn);
			prepStmt.setLong(2, pidm);
			prepStmt.setString(3, termCode);

			ResultSet rs = prepStmt.executeQuery();

			while (rs.next()) {

				grade = rs.getString(1);

			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return grade;

	}

	public static void main(String[] args) throws RemoteException, SQLException {

		GPADb db = new GPADb();
		db.openConn();
		// 19796009
		//db.getCourseAndGrades("201410", "412001409", false, true);
		
        StudentProgram studentProgram = db.getStudentProgram("415001300");
		db.closeConn();
		//System.out.println(studentProgram);
		
		// db.getCouseAndGradesOtherInstitutions("200510", "20043389",
		// true,true);
		// db.openConn();
		// ArrayList mapPrevious = db.gatAllCourseAndGrades("200410", true,
		// true);
		// System.out.println(mapPrevious.size());
		// MySQLConnection conn = new MySQLConnection();
		// conn.addCourseAndGrades(mapPrevious, true);

		// MySQLConnection conn = new MySQLConnection();
		// conn.connectGPADb();
		// conn.addCourseAndGrades(mapPrevious, true);

		// System.out.println(mapPrevious.size());
		GPARemoteService service = new GPARemoteService();
		// service.getConvertedGPA("200410", "408002702", false);
		// service.getConvertedGPA("200410", "03609206", true);

		// long start = System.currentTimeMillis();
		// db.getCourseAndGrades("200410", "408002702", true, true);
		// ArrayList list = service.getCourseAndGrades("200410", "408002702",
		// true, true);
		// service.getDegreeAwarded("408002702");
		// GPA gpa = service.getConvertedGPA("200410","408002702" , true);
		// long end = System.currentTimeMillis();
		// long diff = end - start;
		// System.out.println(diff);
		// System.out.println(gpa.getGpa());
		// double gpa = service.getOldGPA("200410", "408002702");

		// GPADb db = new GPADb();
		// db.openConn();
		// db.insertOldGPA("200410", false);

		System.exit(0);

	}

	public String getSpridenID(long pidm) {

		String sqlstmt = "select spriden_pidm, spriden_id from spriden where spriden_pidm = ?";
		String id = null;
		try {

			PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
			prepStmt.setLong(1, pidm);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()) {
				id = rs.getString(2);
			}

			rs.close();
			prepStmt.close();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		return id;
	}

}
