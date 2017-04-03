/*******************************************************************************
 * MIT License
 *
 * Copyright (c) 2017 Anthony Law
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
package com.github.mob41.osumer.ui.old;

import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import java.awt.Color;
import java.awt.Font;

public class LoginPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2996646468988190391L;
	private JTextField usrFld;
	private JPasswordField pwdFld;

	/**
	 * Create the panel.
	 */
	public LoginPanel() {
		
		JLabel lblNoConfigurationFound = new JLabel("No configuration found for logging in.");
		lblNoConfigurationFound.setFont(new Font("PMingLiU", Font.BOLD, 12));
		lblNoConfigurationFound.setForeground(Color.RED);
		lblNoConfigurationFound.setHorizontalAlignment(SwingConstants.CENTER);
		
		JLabel lblAnOsuAccount = new JLabel("An osu! account is required to download beatmaps.");
		lblAnOsuAccount.setHorizontalAlignment(SwingConstants.CENTER);
		
		JLabel lblYourLoginInformation = new JLabel("Your login information will only used for logging in to osu! forum.");
		lblYourLoginInformation.setHorizontalAlignment(SwingConstants.CENTER);
		
		JLabel lblSeeGithubProjects = new JLabel("See GitHub project's disclaimer for more details.");
		lblSeeGithubProjects.setHorizontalAlignment(SwingConstants.CENTER);
		
		JLabel lblUsername = new JLabel("Username:");
		
		usrFld = new JTextField();
		usrFld.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password:");
		
		pwdFld = new JPasswordField();
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblAnOsuAccount, GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
						.addComponent(lblNoConfigurationFound, GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
						.addComponent(lblYourLoginInformation, GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
						.addComponent(lblSeeGithubProjects, GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(lblPassword, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblUsername, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(pwdFld, GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
								.addComponent(usrFld, GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE))))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblNoConfigurationFound)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblAnOsuAccount)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblYourLoginInformation)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblSeeGithubProjects)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblUsername)
						.addComponent(usrFld))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblPassword)
						.addComponent(pwdFld))
					.addGap(13))
		);
		setLayout(groupLayout);

	}
	
	protected String getUsername(){
		return usrFld.getText();
	}
	
	protected String getPassword(){
		return new String(pwdFld.getPassword());
	}
}
