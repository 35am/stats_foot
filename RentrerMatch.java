import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class RentrerMatch extends HttpServlet {
	
	private Connection con;

	public void ConnexionBDD()throws Exception{
		/*enregistrement du driver*/
		Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
		/*Connexion à la base : source de données ODBC a le nom Access*/
		String url = "jdbc:odbc:Access";
		String nom = "admin";
		String mdp = "xxx";
		this.con = DriverManager.getConnection(url,nom,mdp);
		
		
	}

	public void DeconnexionBDD()throws Exception{
		/*fermeture des connexions à la BDD*/
		this.con.close();
	}
	public void SelectionEquipe(PrintWriter out, Statement stmt, String eq) throws SQLException {
		/*S�lection de l'equipe � domicile*/		
		out.println("<select name="+eq+" id="+eq+">");
		out.println("equipe : "+eq);

		String query = "select nom from equipe";
		ResultSet rs =stmt.executeQuery(query);
		
		//variable pour stocker le nom de chaque equipe
		String nom;
		while(rs.next()){
			//On change le nom de l'�quipe � chaque tour de boucle
			nom = rs.getString(1);
			out.println("<option value="+nom+">"+nom+"</option>");
			
		}
		out.println("</select>");
	
	}
	
	public void Affichage(PrintWriter out, int err,Statement stmt) throws SQLException {
		
		out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//FR\"\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
		out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"fr\">");
		out.println("<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /><link href=\"style.css\" rel=\"stylesheet\" type=\"text/css\" /><title> Pronostiques Football </title></head>");
		out.println("<body><h1 align=\"center\"><font color=\"blue\">Rentrer un match</font></h1><br><br>");
		if(err == 1){
			out.println("<h3 align=center><font color=red>Vous ne pouvez selectionner qu'une seule fois une equipe</font></h3>");
		}
		
		out.println("<br><form method=post action=RentrerResultatMatch> <p>Date	Equipe Domicile	Score Domicile	Score Exterieur	Equipe exterieur</p>");
		out.println("<p><input type=text name=jour size=2>/<input type=text name=mois size=2>/<input type=text name=annee size=4>");
		
		//Selection Equipe Domicile
		this.SelectionEquipe(out,stmt,"Domicile");

		/*Entr�e des scores des �quipes*/
		out.println("<input type=text name=ScoreD size=2><input type=text name=ScoreE size=2>");

		//S�lection Equipe Exterieur
		this.SelectionEquipe(out,stmt,"Exterieur");

		out.println("</p><br><p align=center><input type=submit name=envoyer><input type=reset name=effacer></p>");
		out.println("</body></html>");
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException {
		PrintWriter out = res.getWriter();		
		try{
			doPost(req,res);
		}catch(ServletException e){
			out.println(e.getMessage());
		}			
  	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		HttpSession session = req.getSession(true);		
		Integer erreur = (Integer)session.getAttribute("erreur");
		erreur = new Integer(erreur == null ? 0 : erreur.intValue());
		session.setAttribute("erreur",erreur);
		
		Statement stmt = null;
		PrintWriter out = res.getWriter();

		/*Connexion � la base de donn�es*/		
		try{
			this.ConnexionBDD();
		}catch(Exception e){
			out.println("Echec de connexion � la BDD");
		}

		try{
			/*execution d'une requete*/		
			stmt = this.con.createStatement();
		}catch(SQLException e){
			e.getMessage();
		}

		
		res.setContentType("text/html");
		/*try{
			this.Affichage(out,0,stmt);
		}catch(SQLException sql){
			sql.getMessage();
		}*/
		
		/*Test si l'utilisateur a s�lectionn� 2 fois la m�me �quipe*/
		try{			
			// traitements
			if(erreur != 0){
				this.Affichage(out,1,stmt);
				session.setAttribute("erreur",0);
			}else{
				this.Affichage(out,0,stmt);
			}
		}catch(SQLException e){
			try{
				this.Affichage(out,0,stmt);
			}catch(Exception er){
				er.getMessage();
			}
		}		
		
		try{
			this.DeconnexionBDD();
		}catch(Exception e){
			out.println("Echec de d�connexion � la BDD");
		}
	}

}
