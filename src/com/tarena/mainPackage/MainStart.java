package com.tarena.mainPackage;

import java.awt.Color;

import javax.swing.JFrame;

import com.tarena.tetris.Tetris;

public class MainStart {
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		Tetris tetris = new Tetris();
		tetris.setBackground(new Color(0x0000ff));
		frame.add(tetris);
//		frame.setSize(540,586);
		frame.setSize(530,578);
		frame.setTitle("¶íÂÞË¹·½¿é");
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setResizable(false);
		tetris.action();
	}
}
