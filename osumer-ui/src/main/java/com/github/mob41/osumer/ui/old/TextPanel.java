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
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import java.awt.Font;

public class TextPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7991341001008227901L;

	/**
	 * Create the panel.
	 */
	public TextPanel(String text) {
		
		JLabel lblUpdateChangelog = new JLabel("Update description/change-log:");
		
		JScrollPane scrollPane = new JScrollPane();
		
		JLabel lblDoYouWant = new JLabel("Do you want to install this update?");
		lblDoYouWant.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblDoYouWant.setHorizontalAlignment(SwingConstants.CENTER);
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
		    groupLayout.createParallelGroup(Alignment.TRAILING)
		        .addGroup(groupLayout.createSequentialGroup()
		            .addContainerGap()
		            .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
		                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
		                .addComponent(lblUpdateChangelog, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
		                .addComponent(lblDoYouWant, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE))
		            .addContainerGap())
		);
		groupLayout.setVerticalGroup(
		    groupLayout.createParallelGroup(Alignment.LEADING)
		        .addGroup(groupLayout.createSequentialGroup()
		            .addContainerGap()
		            .addComponent(lblUpdateChangelog)
		            .addPreferredGap(ComponentPlacement.RELATED)
		            .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
		            .addPreferredGap(ComponentPlacement.RELATED)
		            .addComponent(lblDoYouWant)
		            .addContainerGap())
		);
		
		JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		setLayout(groupLayout);
		textArea.setEditable(false);
		textArea.setText(text);

	}
}
