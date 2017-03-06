package components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Paint;
import java.awt.Rectangle;

import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CompassPlot;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.general.ValueDataset;

public class Compass extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private ChartPanel chart_panel;
	private CompassPlot compass_plot;
	private ValueDataset dataset;
	private JFreeChart compass_chart;
	
	public Compass() {
		super();
		
		dataset = new DefaultValueDataset(new Double(4.5));
		
		compass_plot = new CompassPlot(dataset);
		compass_plot.setSeriesNeedle(0);
		compass_plot.setSeriesPaint(0, Color.RED);
		compass_plot.setSeriesOutlinePaint(0, Color.RED);
		compass_plot.setDrawBorder(false);
		compass_plot.setBackgroundPaint(new Color(255,255,255));
		compass_plot.setBackgroundAlpha(0.3f);
		
		compass_chart = new JFreeChart(compass_plot);
		
		chart_panel = new ChartPanel(compass_chart);
		chart_panel.setPreferredSize(new Dimension(300, 300));
		chart_panel.setEnforceFileExtensions(false);
		chart_panel.setOpaque(false);
		
		this.add(chart_panel);
		this.setOpaque(false);
		this.setBounds(0, 0, 300, 300);
	}
}
