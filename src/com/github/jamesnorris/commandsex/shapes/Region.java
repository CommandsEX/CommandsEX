package com.github.jamesnorris.commandsex.shapes;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Region {
	private Location loc1, loc2, loc3, loc4, loc5, loc6, loc7, loc8;
	private World world;
	private int highX, highZ, lowX, lowZ, lowY, highY;
	
	/**
	 * Creates a new 3D region from 2 corner points.
	 * @param loc1 The first corner
	 * @param loc2 The second corner
	 */
	public Region(Location loc1, Location loc2) {
		world = loc1.getWorld();
		this.loc1 = loc1;
		this.loc2 = loc2;
		
		int loc1X = loc1.getBlockX();
		int loc1Y = loc1.getBlockY();
		int loc1Z = loc1.getBlockZ();
		int loc2X = loc2.getBlockX();
		int loc2Y = loc2.getBlockY();
		int loc2Z = loc2.getBlockZ();
		
		highX = (loc1X > loc2X) ? loc1X : loc2X;
		lowX = (loc1X <= loc2X) ? loc1X : loc2X;
		highY = (loc1Y > loc2Y) ? loc1Y : loc2Y;
		lowY = (loc1Y <= loc2Y) ? loc1Y : loc2Y;
		highZ = (loc1Z > loc2Z) ? loc1Z : loc2Z;
		lowZ = (loc1Z <= loc2Z) ? loc1Z : loc2Z;
		
		loc3 = world.getBlockAt(loc2X, highY, loc1Z).getLocation();
		loc4 = world.getBlockAt(loc1X, highY, loc2Z).getLocation();
		loc5 = world.getBlockAt(loc1X, lowY, loc1Z).getLocation();
		loc6 = world.getBlockAt(loc2X, lowY, loc2Z).getLocation();
		loc7 = world.getBlockAt(loc2X, lowY, loc1Z).getLocation();
		loc8 = world.getBlockAt(loc1X, lowY, loc2Z).getLocation();
	}
	
	/**
	 * Gets the corner that matches the given number.
	 * Please note that the corners may be in a different order for each region.
	 * @param corner The corner number from 1-8
	 * @return The corner corresponding to the number
	 */
	public Location getCorner(int corner) {//TODO arrange the locations around the rectangular prism
		Location[] locs = new Location[] { loc1, loc2, loc3, loc4, loc5, loc6, loc7, loc8 };
		return locs[corner];
	}
	
	/*
	 * Checks a 1D rectangle for overlap.
	 */
	private boolean overlap_1D(double aLow, double aHigh, double bLow, double bHigh) {
	    if (aLow <= bLow) 
	    	return (bLow <= aHigh);
	    return (aLow <= bHigh);
	}
	
	/**
	 * Checks if the given region touches or overlaps this region.
	 * @param other The region to check for
	 * @return Whether or not they touch or overlap
	 */
	public boolean overlaps(Region other) {
	    boolean Xs = overlap_1D(lowX, highX, other.getLowestX(), other.getHighestX());
	    boolean Ys = overlap_1D(lowY, highY, other.getLowestY(), other.getHighestY());
	    boolean Zs = overlap_1D(lowZ, highZ, other.getLowestZ(), other.getHighestZ());
	    return (Xs && Ys && Zs);  	
	}
	
	/**
	 * Checks if the location is contained inside the region.
	 * @param loc The location to check for
	 * @return Whether or not the location is contained in the region
	 */
	public boolean contains(Location loc) {
	    boolean Xs = overlap_1D(lowX, highX, loc.getBlockX(), loc.getBlockX());
	    boolean Ys = overlap_1D(lowY, highY, loc.getBlockY(), loc.getBlockY());
	    boolean Zs = overlap_1D(lowZ, highZ, loc.getBlockZ(), loc.getBlockZ());
		return (Xs && Ys && Zs);	
	}
	
	/**
	 * Checks if the player is contained inside the region.
	 * @param p The player to check for
	 * @return Whether or not the player is contained in the region
	 */
	public boolean contains(Player p) {
		return contains(p.getLocation());
	}
	
	/**
	 * Gets the highest X of all 8 locations in this region.
	 * @return The highest X value for this region
	 */
	public int getHighestX() {
		return highX;
	}
	
	/**
	 * Gets the lowest X of all 8 locations in this region.
	 * @return The lowest X value for this region
	 */
	public int getLowestX() {
		return lowX;
	}
	
	/**
	 * Gets the highest Y of all 8 locations in this region.
	 * @return The highest Y value for this region
	 */
	public int getHighestY() {
		return highY;
	}
	
	/**
	 * Gets the lowest Y of all 8 locations in this region.
	 * @return The lowest Y value for this region
	 */
	public int getLowestY() {
		return lowY;
	}
	
	/**
	 * Gets the highest Z of all 8 locations in this region.
	 * @return The highest Z value for this region
	 */
	public int getHighestZ() {
		return highZ;
	}
	
	/**
	 * Gets the lowest Z of all 8 locations in this region.
	 * @return The lowest Z value for this region
	 */
	public int getLowestZ() {
		return lowZ;
	}
}
