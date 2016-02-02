package com.starwood.anglerslong;

import java.io.Serializable;

public class ChecklistItem implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private boolean checked;

	public ChecklistItem(String name, boolean checked) {
		this.name = name;
		this.checked = checked;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
}
