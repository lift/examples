/*
 * Copyright 2008-2010 WorldWide Conferencing, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.liftweb {
package jpademo {
package model {

import org.junit.Test
import org.junit.Before
import org.junit.After
import org.junit.Assert._

import javax.persistence._

class TestJPAWeb {
  var emf : EntityManagerFactory = _

  @Before
  def initEMF () = {
    try {
      emf = Persistence.createEntityManagerFactory("jpaweb")
    } catch {
      case e: Exception => {
	def printAndDescend(ex : Throwable) : Unit = {
	  println(e.getMessage())
	  if (ex.getCause() != null) {
	    printAndDescend(ex.getCause())
	  }
	}
	printAndDescend(e)
      }
    }
  }

  @After
  def closeEMF () = {
    if (emf != null) emf.close()
  }

  @Test
  def save_stuff () = {
    var em = emf.createEntityManager()

    val tx = em.getTransaction()

    tx.begin()

    val author = new Author
    author.name = "Chuck"

    em.persist(author)

    val book = new Book
    book.title = "Huh?"
    book.published = new _root_.java.util.Date
    book.author = author
    book.genre = Genre.Mystery

    em.persist(book)

    tx.commit()

    em.close()

    // Re-open and query
    em = emf.createEntityManager()

    val retrieved = em.createNamedQuery("findAllBooks").getResultList().asInstanceOf[java.util.List[Book]]

    assertEquals(1, retrieved.size())
    assertEquals(Genre.Mystery, retrieved.get(0).genre)

    println("Found " + retrieved.get(0).title)

    // clean up
    em.getTransaction().begin()

    em.remove(em.getReference(classOf[Book],book.id))
    em.remove(em.getReference(classOf[Author],author.id))

    em.getTransaction().commit()

    em.close()
  }
}
}
}
}
