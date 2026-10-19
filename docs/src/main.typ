#set page(
  margin: (
    top: 2cm,
    bottom: 2cm,
    left: 2cm,
    right: 2cm
  )
)

#set text(
  lang: "ru",
  size: 14pt,
  font: "New Computer Modern",
)

#show heading: it => {
  it
  v(0.5em)
}
#show heading: it => text(fill: navy, it)
#show heading.where(level: 1): it => text(size: 18pt, it)
#show heading.where(level: 2): it => text(size: 16pt, it)
#show heading.where(level: 3): it => {
  set block(below: 0.5em)
  text(size: 14pt, it)
}
// #show heading.where(level: 4): it => {
//   block(
//     below: 0.6em, 
//     above: 1em,
//     it.body
//   )
// }


#include "front_page.typ"

// #let funcReq(
//   id,
//   title,
//   priority,
//   source,
//   dependencies,
//   description,
  
// )

#set page(
  numbering: "1",
  number-align: center
)

#outline(
  depth: 4
)
#pagebreak()

#set par(justify: true)
#include "content.typ"
