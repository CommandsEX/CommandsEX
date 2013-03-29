package com.github.jamesnorris.commandsex.shapes;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class Sphere {//TODO annotations
	private ArrayList<Block> sphere = new ArrayList<Block>();
	private Location center;
	private int radius;
	
	public Sphere(Location center, int radius) {
		this.center = center;
		this.radius = radius;
	     for(int X = -radius; X < radius; X++) {
	         for(int Y = -radius; Y < radius; Y++) {
	             for(int Z = -radius; Z < radius; Z++) {
	                 if(Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) {
	                	 Block block = center.getWorld().getBlockAt(X + center.getBlockX(), Y + center.getBlockY(), Z + center.getBlockZ());
	                	 sphere.add(block);
	                 }
	             }
	         }
	     }
	}
	
	public Location getCenter() {
		return center;
	}
	
	public int getRadius() {
		return radius;
	}
	
	public ArrayList<Block> getBlocks() {
		return sphere;
	}
	
	private boolean isInside(int X, int Y, int Z) {
		return Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius;
	}
	
	public boolean overlaps(Sphere other) {
		for (Block block : other.getBlocks())
		    if (contains(block.getLocation()))
		            return true;
		return false;
	}
	
	public boolean contains(Location loc) {
		return isInside(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}
}
