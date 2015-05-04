//javac -classpath .;mysqlconnector.jar;sendgrid.jar;miglayout.jar openTicketDetailsWindow.java

import javax.swing.*;
import java.util.*;
import java.awt.Color;
import java.awt.event.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.Desktop;  
import java.io.IOException;  
import java.net.URI;  
import java.net.URISyntaxException;  


//MigLayout
import net.miginfocom.layout.Grid;
import net.miginfocom.swing.MigLayout;

public class openTicketDetailsWindow extends JFrame{
	

	protected Ticket ticket;
	protected User user;

	protected JButton completeTicket;
	protected JButton refer;
	protected JButton close;


	//Ticket Details Side
	protected JTextArea timeEntered;
	protected JTextArea issueName;
	protected JTextArea description;
	protected JTextArea priority;
	protected JTextArea screenShots;
	protected JTextArea noOfReferals;
	protected JTextArea timeEnteredt;
	protected JTextArea issueNamet;
	protected JTextArea descriptiont;
	protected JTextArea priorityt;
	protected JTextArea screenShotst;

	//User Details Side
	protected JTextArea name;
	protected JTextArea email;
	protected JTextArea phonenumber;
	protected JTextArea namet;
	protected JTextArea emailt;
	protected JTextArea phonenumbert;

	protected JTextArea noOfReferalst;

	protected Color defaultGrey = new Color(238, 238, 238);


	public openTicketDetailsWindow(Ticket t){
		super("Ticket Number: "+t.getTicketID()+" Details");
		
		if (t == null) {
				System.out.println("null ticket passed to window");
			}
		
		ticket = t;
		user = mySqlConnector.getUserbyID(ticket.getUserID());

		
		setLayout(new MigLayout("wrap 2"));
		add(new buttons(),"dock south");
		add(new ticketDetails());
		add(new personDetails(),"growy");



		completeTicket.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e){
					int selectedOption = JOptionPane.showConfirmDialog(null, 
                                  "Are you sure you want to complete this ticket?", 
                                  "Are you sure?", 
                                  JOptionPane.YES_NO_OPTION); 
						if (selectedOption == JOptionPane.YES_OPTION) {
							if(mySqlConnector.completeTicket(ticket.getTicketID())){
								JOptionPane.showMessageDialog(null,"Ticket Successfully Completed","Ticket Successfully Completed",JOptionPane.PLAIN_MESSAGE);

								
								User user = mySqlConnector.getUserbyID(t.getUserID());
								if(Emailer.ticketCompleted(t,user)){
								}
								else{
									JOptionPane.showMessageDialog(null,"Error sending confirmation email","Confirmation e-mail not sent!, Please Contact user manually!",JOptionPane.ERROR_MESSAGE);	
								}

								//Email TO DO	

								setVisible(false);
								dispose();

								EngineerApplication.refreshTable();

							}

						}
				}
			});

		close.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				setVisible(false);
				dispose();
				EngineerApplication.refreshTable();
			}


		});
		
		email.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				if(sendEmail()){}
				else{
					JOptionPane.showMessageDialog(null,"Error opening mail application","Error opening mail application",JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		refer.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				
				List<Engineer> engs = mySqlConnector.getAllEngineers();
				
				String[] engineerNames = miscMethods.getListOfEngNames(engs);
				
				String engName = referTo(engineerNames);
				
				mySqlConnector.referTicket(t.getTicketID(),miscMethods.getEngIdFromNameString(engs, engName),(t.getNoOfReferrals()+1));
			}


		});



		setVisible(true);
		pack();




	}
	private class ticketDetails extends JPanel{
		public ticketDetails(){
			setLayout(new MigLayout("wrap 2"));


			timeEnteredt = new JTextArea("Time Submitted: ");
			timeEnteredt.setBackground(defaultGrey);
			add(timeEnteredt);
			timeEntered = new JTextArea(miscMethods.convertTime(ticket.getTimeEntered()),1,25);
			add(timeEntered);


			issueNamet = new JTextArea("Issue Type: ");
			issueNamet.setBackground(defaultGrey);
			add(issueNamet);
			issueName = new JTextArea(ticket.getIssueName(),1,25);
			add(issueName);


			descriptiont = new JTextArea("Description: ");
			descriptiont.setBackground(defaultGrey);
			add(descriptiont);
			description = new JTextArea(ticket.getProblemDesc(),4,25);
			description.setLineWrap(true);
			add(description);


			priorityt = new JTextArea("Priority: ");
			priorityt.setBackground(defaultGrey);
			add(priorityt);
			priority = new JTextArea(Integer.toString(ticket.getPriority()));
			add(priority);

			//PUT PHOTOS IN HERE!

			noOfReferalst = new JTextArea("Number of Referals: ");
			noOfReferalst.setBackground(defaultGrey);
			add(noOfReferalst);
			noOfReferals = new JTextArea(Integer.toString(ticket.getNoOfReferrals()));
			add(noOfReferals);

			timeEnteredt.setEditable(false);
			timeEntered.setEditable(false);
			issueNamet.setEditable(false);
			issueName.setEditable(false);
			descriptiont.setEditable(false);
			description.setEditable(false);
			priorityt.setEditable(false);
			priority.setEditable(false);
			noOfReferalst.setEditable(false);
			noOfReferals.setEditable(false);


		}
	}
	private class personDetails extends JPanel{
		public personDetails(){
			setLayout(new MigLayout("wrap 2"));

			namet = new JTextArea("Name of User: ");
			namet.setBackground(defaultGrey);
			add(namet);
			name = new JTextArea(user.getTitle()+" "+user.getForename()+" "+user.getSurname(),1, 20);
			add(name);

			emailt = new JTextArea("E-Mail Address: ");
			emailt.setBackground(defaultGrey);
			add(emailt);
			email = new JTextArea(user.getEmail());
			
			add(email);
		

			phonenumbert = new JTextArea("Phone Number: ");
			phonenumbert.setBackground(defaultGrey);
			add(phonenumbert);
			phonenumber = new JTextArea(user.getPhoneNumber());
			add(phonenumber);

			namet.setEditable(false);
			name.setEditable(false);
			emailt.setEditable(false);
			email.setEditable(false);
			phonenumbert.setEditable(false);
			phonenumber.setEditable(false);


		}

	}
	private class buttons extends JPanel{
		public buttons(){
			completeTicket = new JButton("Complete");
			refer = new JButton("Refer Ticket");	
			close = new JButton("Close");


			add(completeTicket);
			add(refer);
			add(close);
		}

	}
	
	private boolean sendEmail(){
	
		 Desktop desktop = Desktop.getDesktop();  
          String url = "";  
          URI mailTo;  
           try {  
               url = "mailTo:"+user.getEmail()+"?subject="+"Ticket%20-%20"+ticket.getTicketID()+"%20Response";  
                mailTo = new URI(url);  
                desktop.mail(mailTo);  
				return true;
           } catch (URISyntaxException e) {  
                e.printStackTrace();  
				return false;
           } catch (IOException e) {  
                 e.printStackTrace(); 
				 return false;
            }  
	
	
	}
	
		static String referTo(String[] choices) {
        String s = (String) JOptionPane.showInputDialog(
                null,
                "Select an Engineer To Refer this ticket to",
                "Refer Ticket",
                JOptionPane.PLAIN_MESSAGE,
                null,
                choices,
                choices[0]);
        return s;
    }




}