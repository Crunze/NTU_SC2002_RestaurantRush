guide me with stage 3
I’ve read both files. I haven’t seen your Stage 2 code, so this is a plan against the spec, not a diff against your classes. Stage 3 is due today at 11:59 PM SGT, so I’ve put the order of work first.
Order of work
Branch from the Stage 2 tag so stage-2 stays untouched: git checkout -b stage-3-dev stage-2.
Refactor pricing and menu items first, then confirm your Stage 2 reproducible runs still give identical results. That’s your “what still works” evidence.
Add ComboMeal, then Host, then Happy Hour, committing after each.
Update the CLI to add Host and Happy Hour prompts.
Save a Stage 3 victory run and defeat run as test notes, tag stage-3, then run git diff stage-2 stage-3 --stat for the README.
Do the class diagram (it must match the code), the sequence diagram, then the report and video.
The three features
ComboMeal
Make a combo a menu entry built from two existing items, with getPriceCents() and getPrepUnits(). Order should depend only on that abstraction.
If your Stage 1 menu is an enum, that’s your one justified change to earlier code: an enum can’t hold a composed item. Keep the enum for the base dishes and add a combo class beside it.
Price in integer cents: (sum * 9 + 5) / 10. Salad + Pasta gives 2430 cents and 3 units, matching the spec.
Decide whether a combo can be two of the same item, and document it. Also document your customer rule, such as “turn-7 and turn-13 arrivals choose a combo”, or seeded randomness.
Put the choice inside the customer object (customer.chooseOrder(menu)), so the Critic’s preference from Stage 2 still works and the controller has no type checks.
A 3-unit combo takes 3 Chef turns, so check it doesn’t make your victory run impossible.
Host
Extract the “seat one queued customer at a free table” logic into one place, such as a method on the restaurant or a small seating service, that both Host and Waiter call. The Waiter then needs almost no change.
The Host acts in addition to the Waiter’s task, so the two must not share a task counter.
Document the order within a turn. I’d suggest Host, then Waiter, then Chef. That lets the Waiter take an order from a customer the Host just seated, which is a meaningful manager choice.
If the Host seats the only queued customer, the Waiter’s seat option becomes invalid. Explain why and re-prompt without consuming the task.
Happy Hour
Make it its own object: activated (once per game), remainingTurns, isActive(), and advanceTurn().
Activation is a manager action at the start of a turn, not a staff task. Say so in your docs.
Apply it before step 1: activate, then recover 15 satisfaction for customers waiting for a table or food, capped at 100. That includes queued customers and seated customers whose food isn’t served yet. Do not apply it to served, eating, or ready-to-pay customers. The clamp belongs in the customer object, customer.recoverSatisfaction(15).
Lock the price when the order is taken, and store it on the Order. Never recompute at payment.
Use a small pricing pipeline in a fixed order: combo discount, then the customer’s own discount (VIP is 10%, 0% for others), then Happy Hour. The customer supplies its discount polymorphically.
Choose one rounding rule and use it at every step. Test values:
VIP Burger under Happy Hour: 1500, then 1350, then 1080, which is $10.80 (matches the spec).
VIP with a Salad + Pasta combo under Happy Hour: 2430, then 2187, then 1749.6, which rounds to 1750 ($17.50). This case is where your rounding rule matters.
Unit tests
Combo price and units for all three combo pairs.
VIP + Happy Hour price.
Happy Hour activates only once, lasts exactly two turns, and doesn’t affect orders placed before or after it.
Recovery caps at 100 and skips served customers.
Host seating plus Waiter seating in one turn.
Invalid actions don’t consume the task.
Report and README evidence
For 2 → 3, list new classes and their responsibilities, modified classes with reasons, and the regression checks. The expected modified set is menu/order, Customer (discount and recovery hooks), the turn controller, and the CLI. If the controller changed more than registration and a new step, explain why.
Also save your AI chats in order at ai-history/stage-3/chat-01.md on your own branch.
If you paste your Stage 2 Order, menu, Customer hierarchy and turn controller, I can show you the concrete changes and help draft the Stage 3 class and sequence diagrams.

if there are many branches on my GitHub, branches that my friends and I work separately on, then we push our respective branches on a branch called cherrypicking, then is cherrypicking the branch that has the most recent changes and should be the one I run the test case on (this is for the restaurant rush game)
Not automatically. A branch called cherrypicking only has the most recent changes if everyone’s work has actually been merged or cherry-picked into it, and it only stays current if someone keeps doing that.
What determines “most recent”
Each branch is its own timeline. cherrypicking contains a friend’s commit only if that commit was cherry-picked or merged into it. If your friend pushed to their own branch after the last time anyone updated cherrypicking, their newest work isn’t there yet. Also, cherry-picking copies individual commits, so cherrypicking can end up with some of a branch’s work but not all of it.
How to check
Fetch everything first:
git fetch --all
See when each branch was last updated:
git branch -r --sort=-committerdate --format='%(committerdate:short) %(refname:short)'
This lists remote branches newest first. On GitHub, the Branches page shows the same thing.
See what a friend’s branch has that cherrypicking doesn’t:
git log origin/cherrypicking..origin/friend-branch --oneline
If this prints commits, cherrypicking is missing them. If it prints nothing, everything on that branch is already in cherrypicking. (Note: for cherry-picked commits, git sees them as different commits, so they may still show up even if the changes are there. git cherry -v origin/cherrypicking origin/friend-branch handles that better, marking commits already applied with - and missing ones with +.)
What to test
Test the branch you intend to be the integrated version of the game, which is probably cherrypicking, but only after confirming it contains everyone’s latest changes. Running your test cases on it is reasonable as long as it’s up to date. If you want to catch problems from combining everyone’s work (conflicts, features breaking each other), testing the integration branch is exactly the right move. If you only want to verify your own feature, test your own branch.
A good habit is to agree with your friends on when to update cherrypicking, then pull it and run the tests right after each update.

how can I open the most recent code in my github
It depends on where you want to open it, but here are the quickest ways.
On GitHub (browser)
Go to github.com and open your profile. Your “Repositories” tab sorts by most recently updated by default.
Your dashboard (the github.com home page) also shows a “Recent activity” feed and recent repositories in the left sidebar.
For the latest commit in a repo, open the repo and click the commit count or “Commits” link above the file list. The top entry is the newest.
On your computer (local)
If you’ve already cloned the repo, open the folder in your editor and run git pull to get the latest changes.
If you haven’t cloned it, click the green Code button on the repo page, copy the URL, and run git clone <url>.
To check what you worked on most recently, git log -5 in a terminal shows your last five commits.
In the cloud (no setup)
Open the repo and press the . (period) key to launch GitHub’s web editor, or use Code → Codespaces for a full cloud dev environment.
If you tell me whether you’re on a computer or phone, and whether you want to view, edit, or run the code, I can give you more specific steps.
I want to clone it my terminal is like that now ## cd /Users/arissalim/Desktop/NTU_SC2002_RestaurantRush ##
Last login: Fri Nov 13 17:06:18 on ttys000
Restored session: Sat Nov 14 00:06:12 +08 2026
arissalim@192 ~ %
Your terminal is at the home folder (~), and that path in your header suggests the project folder is NTU_SC2002_RestaurantRush on your Desktop. Here’s how to clone it.
1. Get the repo URL
On the repo’s GitHub page, click the green Code button and copy the HTTPS URL. It looks like:
https://github.com/<your-username>/NTU_SC2002_RestaurantRush.git
2. Go to where you want it
bash
cd ~/Desktop
3. Clone it
bash
git clone https://github.com/<your-username>/NTU_SC2002_RestaurantRush.git
4. Enter the folder
bash
cd NTU_SC2002_RestaurantRush
A few things to watch for:
If a folder with that name already exists on your Desktop, git clone will fail (or complain the destination isn’t empty). In that case either cd into it and run git pull to update it, or clone into a different name: git clone <url> RestaurantRush_new.
If it’s a private repo, GitHub will ask you to authenticate. Passwords no longer work for this, so use a personal access token as the password, or set up the GitHub CLI with gh auth login and then gh repo clone <your-username>/NTU_SC2002_RestaurantRush.
If you get git: command not found, install the Apple developer tools with xcode-select --install.
Once it’s cloned, you can open it in VS Code with code . from inside the folder (if you have the code command set up), or just drag the folder into your editor.
wait if there are many branches then which one should I clone
Cloning gets you the whole repo, including all branches. By default, Git checks out the repo’s default branch (usually main or master), and the other branches are downloaded too but not switched to.
To see what branches exist after cloning
bash
git branch -a
Your current branch has a * next to it, and the remotes/origin/... entries are the other branches on GitHub.
To switch to a different branch
bash
git checkout <branch-name>
or the newer form:
bash
git switch <branch-name>
To clone a specific branch straight away
bash
git clone -b <branch-name> https://github.com/<your-username>/NTU_SC2002_RestaurantRush.git
Which one should you pick?
For “the most recent code,” it’s usually not main. Whoever was last working may have pushed to a feature branch.
On the GitHub repo page, click the branch dropdown (top left of the file list) and look at All branches. It shows when each was last updated.
Or after cloning, run:
bash
git branch -a --sort=-committerdate
This lists branches with the most recently committed one at the top.
Since this looks like a group project for SC2002, a good rule of thumb is: main has the stable/merged version, and a feature branch has whatever your teammates are currently building. If you’re not sure, ask your team which branch they’re using, or check the latest commit dates on each branch.
so if I want the code that is most recent, I change to that branch and get the https:// link then clone it correct?
Not quite. The HTTPS link is the same for every branch, because it points to the whole repository. Switching branches on GitHub’s website doesn’t change the link.
What you do is:
1. Clone the repo (same URL as before)
bash
cd ~/Desktop
git clone https://github.com/<your-username>/NTU_SC2002_RestaurantRush.git
cd NTU_SC2002_RestaurantRush
2. Find the most recently updated branch
bash
git branch -a --sort=-committerdate
The one at the top is the newest.
3. Switch to it
bash
git switch <branch-name>
If it’s listed as remotes/origin/feature-x, just use feature-x as the name.
Shortcut: if you already know the branch name, you can do steps 1 and 3 together:
bash
git clone -b <branch-name> https://github.com/<your-username>/NTU_SC2002_RestaurantRush.git
One thing to note: the branch dropdown on GitHub is still useful, but only for finding out which branch is newest (under All branches, which shows last-updated times). It’s not where you get the link.
for some reason I cant see the stage 3 changes lehhh whats gg on
Most likely you’re on the wrong branch, or you haven’t pulled the latest changes. Let’s check a few things in your terminal, from inside the project folder.
1. See which branch you’re on
bash
git branch
The one with * is your current branch. If “stage 3” lives on a different branch, you won’t see it here.
2. Fetch everything from GitHub and list all branches
bash
git fetch --all
git branch -a --sort=-committerdate
Look for something like stage3, stage-3, or similar. If it’s there, switch to it:
bash
git switch <branch-name>
3. If you’re already on the right branch, pull the latest
bash
git pull
4. Check whether the stage 3 commits actually exist
bash
git log --oneline -10
If you don’t see commits mentioning stage 3, they may not have been pushed yet, or they’re on another branch.
Other common causes
Your teammate committed but forgot to git push, so it’s only on their laptop.
The changes were merged into main but you cloned an older branch (fix: git switch main && git pull).
Your editor is showing a stale view. Close and reopen the folder after switching branches.
If you paste the output of git branch -a --sort=-committerdate and git log --oneline -5, I can tell you exactly where the stage 3 changes are.
fatal: not a git repository (or any of the parent directories): .gitfatal: not a git repository (or any of the parent directories): .git 
I got this
That error means your terminal isn’t inside a cloned repo. Your prompt earlier showed ~ (the home folder), and git commands only work from inside the project folder.
1. Check where you are
bash
pwd
2. Check whether the folder exists on your Desktop
bash
ls ~/Desktop
Look for NTU_SC2002_RestaurantRush in the list.
If the folder is there, go into it and check it’s a repo:
bash
cd ~/Desktop/NTU_SC2002_RestaurantRush
git status
If git status works, you’re good. Now run git fetch --all and git branch -a --sort=-committerdate to find the stage 3 branch.
If you still get “not a git repository”, the folder was copied or downloaded as a ZIP rather than cloned, so it has no git history. In that case, rename it and clone fresh:
bash
cd ~/Desktop
mv NTU_SC2002_RestaurantRush NTU_SC2002_RestaurantRush_old
git clone https://github.com/<your-username>/NTU_SC2002_RestaurantRush.git
cd NTU_SC2002_RestaurantRush
If the folder isn’t there, you haven’t cloned it yet, so run the clone steps:
bash
cd ~/Desktop
git clone https://github.com/<your-username>/NTU_SC2002_RestaurantRush.git
cd NTU_SC2002_RestaurantRush
Replace <your-username> with the GitHub username that owns the repo (if it’s a teammate’s repo, use theirs).
After that, git fetch --all and git branch -a --sort=-committerdate should work, and you can switch to the stage 3 branch.
On branch main
Your branch is up to date with 'origin/main'.
nothing to commit, working tree cleanOn branch main
Your branch is up to date with 'origin/main'.
nothing to commit, working tree clean
I want to go on arissa-stage3
Good, you’re in a real repo now. You’re on main, and you want arissa-stage3. Run these:
bash
git fetch --all
git switch arissa-stage3
The fetch makes sure Git knows about branches that exist on GitHub. After the switch, you should see something like Switched to a new branch 'arissa-stage3' and branch 'arissa-stage3' set up to track 'origin/arissa-stage3'.
Then pull the latest so you have the newest commits:
bash
git pull
To confirm it worked:
bash
git branch
git log --oneline -5
The * should be next to arissa-stage3, and the log should show your stage 3 commits.
If git switch says the branch doesn’t exist or is invalid:
Run this to see what branches are actually there:
bash
git branch -a --sort=-committerdate
Check the exact spelling (it might be Arissa-stage3, arissa-stage-3, etc.). If it only shows up as remotes/origin/arissa-stage3, this will force it to track the remote one:
bash
git switch -c arissa-stage3 origin/arissa-stage3
If it’s not in the list at all, the branch was never pushed to GitHub. It may only exist on whichever computer you made it on. In that case, go to that computer and run git push -u origin arissa-stage3.

