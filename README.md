# KIT305-Assignment-2

## Test Device
The app was tested using a **Medium Phone emulator running Android 16 (Balaclava)**.

---

## References

The main references used for this assignment were:

- Course materials, especially **Week 5** (used as the base code)
- Code and concepts from **Week 3 and Week 4 Android tutorials**
- Lecture code examples provided during the unit

### Use of Generative AI

I used ChatGPT to help guide development, mainly by asking what tools or approaches to use for specific problems. I would then research those suggestions myself to confirm they were appropriate before implementing them.

- Most AI usage resulted in small, one-off function calls or guidance
- Code sections where AI was used are clearly commented in the source code
- Minimal AI was used in:
  - House
  - Room
  - Space
  - ProductSelect
- More AI assistance was used in:
  - QuoteActivity (however, I read through and understood the code in order to debug and integrate it properly)

Note: I was using university lab computers without a logged-in account, so no conversation history or shareable links are available.

---

## Application Structure

The app is made up of three main Activities:

- **MainActivity**  
  Displays a list of houses

- **MainActivity2**  
  Displays a list of rooms within a selected house

- **MainActivity3**  
  Displays a list of spaces within a selected room

Each of these has a corresponding "Add" Activity used to create and edit entries:
- Add House
- Add Room
- Add Space

### Product Selection Flow

- **ProductSelect Activity**  
  Accessed via:  
  `MainActivity3 → SpaceAdd → ProductSelect`  

  This screen retrieves data from an API and allows the user to select a product.

### Quote Feature

- **QuoteActivity**  
  Can be accessed from any of the main Activities  
  Used to generate and display a quote based on selected data

---

## Notes

- AI-assisted sections are clearly commented in the code
- All implemented features were reviewed and understood before submission
- This README was made with AI also incase you couldnt tell lol, last time i did it manually never again
