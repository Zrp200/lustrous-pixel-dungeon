/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2019 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.zrp200.lustrouspixeldungeon.ui.changelist;

import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import com.zrp200.lustrouspixeldungeon.Assets;
import com.zrp200.lustrouspixeldungeon.LustrousPixelDungeon;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;
import com.zrp200.lustrouspixeldungeon.items.Item;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.scenes.ChangesScene;
import com.zrp200.lustrouspixeldungeon.scenes.PixelScene;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSprite;

import java.text.DateFormat;

import static com.zrp200.lustrouspixeldungeon.ui.Icons.PREFS;
import static com.zrp200.lustrouspixeldungeon.ui.Icons.get;

//not actually a button, but functions as one.
public class ChangeButton extends Component {
	
		public static ChangeButton misc(String... changes) {
			return new ChangeButton(get(PREFS),Messages.get(ChangesScene.class,"misc")).appendList(changes);
		}

        public static ChangeButton bugfix(String... fixes) { // it's flawed but it's still better than nothing
			return new ChangeButton(
					new Image( Assets.SPINNER, 144, 0, 16, 16),
					Messages.get(ChangesScene.class,"bugfixes")
			).appendList(fixes);
        }

	public static ChangeButton devCommentary(Image devIcon, Milestone release, String commentary, Milestone... eventsToCompare) {
		StringBuilder message = new StringBuilder();
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);

		if(release != null) { // compare release date to milestones if possible
			message.append( "_-_ Released on " )
					.append(  dateFormat.format( release.releaseDate )  )
					.append("\n");

			if ( eventsToCompare != null ) for ( Milestone event : eventsToCompare ) {
				message.append("_-_ ");

				// convert milliseconds to days
				long daysSinceEvent = (release.releaseDate.getTime() - event.releaseDate.getTime()) / 86400000;

				message.append(daysSinceEvent)
						.append(" day")
						.append(daysSinceEvent != 1 ? "s" : "") // "1 days" makes no sense.
						.append(" after ").append(event.name).append("\n");
			}
			message.append("\n");
		}
		message.append(commentary == null ? "Dev commentary will be added here in the future." : commentary); // addAllChanges commentary
		return new ChangeButton( devIcon,"Developer Commentary", message.toString() );
	}
	public static ChangeButton devCommentary(Milestone release, String commentary, Milestone... eventsToCompare) {
		return devCommentary(new Image(Assets.ZRP200),release,commentary,eventsToCompare);
	}

	protected Image icon;
	protected String title;
	protected String message;
	
		ChangeButton( Image icon, String title, String... messages){
		super();
		
		this.icon = icon;
		add(this.icon);
		
		this.title = Messages.titleCase(title);

			this.message = "";
			appendLines(messages);
		
		layout();
	}
	
		ChangeButton( Item item, String... messages ){
			this( new ItemSprite(item), item.name(), messages);
		}

        ChangeButton(Buff buff, String... messages) {
			this( buff.getLargeIcon(), buff.toString(), messages);
		}

		ChangeButton appendList(String... items ) {
			for(String item : items)
				appendLine("_-_ " + item);
			return this;
		}
		public ChangeButton appendLine(String message) {
		    if (message != null && !message.equals("")) appendLine();
		    return append(message);
		}
		public ChangeButton appendLine() {
			return append("\n");
		}
		public ChangeButton appendLines(String... messages) {
		    for(String message : messages) appendLine(message);
		    return this;
        }
        public ChangeButton appendLines(int lines) {
			for(int line=0; line<lines; line++) {
				appendLine();
			}
			return this;
		}
		public ChangeButton append(String message) {
		    this.message += message;
		    return this;
	}
	
	protected void onClick() {
		LustrousPixelDungeon.scene().add(new ChangesWindow(new Image(icon), title, message));
	}
	
	@Override
	protected void layout() {
		super.layout();
		
		icon.x = x + (width - icon.width) / 2f;
		icon.y = y + (height - icon.height) / 2f;
		PixelScene.align(icon);
	}
}